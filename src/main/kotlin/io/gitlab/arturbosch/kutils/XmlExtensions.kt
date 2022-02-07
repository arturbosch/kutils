@file:Suppress("detekt.TooManyFunctions")

package io.gitlab.arturbosch.kutils

import java.io.Reader
import java.io.Writer
import java.util.Stack
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.XMLEvent

/**
 * When using the underlying [XMLStreamReader] be aware of common streaming pitfalls.
 *
 *  - retrieving the tag content the attributes are skipped, later asking for attributes throws [IllegalStateException]
 *  - retrieving the tag content consumes the [XMLEvent.END_ELEMENT] event.
 */
inline fun <reified T : Any> Reader.streamXml(init: (XMLStreamer.() -> Unit)) =
    XMLStreamer(XMLInputFactory.newFactory().createXMLStreamReader(this)).run {
        init()
        stream()
        result?.invoke() as? T ?: throw IllegalStateException(
            "'${T::class}' expected to return. Make sure to end streaming with an 'onFinish()' call."
        )
    }

private const val FIRST_XML_EVENT_NUMBER = 1
private const val NUMBER_OF_XML_EVENTS = 15

class XMLStreamer(
    private val reader: XMLStreamReader
) {

    var result: (() -> Any)? = null
    private val anyEventsToActions: MutableMap<Int, MutableList<XMLStreamReader.() -> Unit>> = mutableMapOf()
    private val tagsToActions: MutableMap<Int, MutableList<Event>> = mutableMapOf()

    data class Event(val tagName: String, val action: XMLStreamReader.() -> Unit)

    fun onTag(name: String, action: XMLStreamReader.() -> Unit) {
        tagsToActions.getOrPut(XMLEvent.START_ELEMENT) { ArrayList() }.add(Event(name, action))
    }

    fun onTagEnd(name: String, action: XMLStreamReader.() -> Unit) {
        tagsToActions.getOrPut(XMLEvent.END_ELEMENT) { ArrayList() }.add(Event(name, action))
    }

    /**
     * See [XMLEvent] to register to specific events.
     */
    fun onAny(eventType: Int, action: XMLStreamReader.() -> Unit) {
        require(eventType in FIRST_XML_EVENT_NUMBER..NUMBER_OF_XML_EVENTS)
        anyEventsToActions.getOrPut(eventType) { ArrayList() }.add(action)
    }

    fun onFinish(returns: () -> Any) {
        result = returns
    }

    fun stream() {
        reader.apply {
            check(eventType == XMLEvent.START_DOCUMENT)
            while (hasNext()) {
                val eventType = next()
                anyEventsToActions[eventType]?.forEach { it.invoke(this) }
                tagsToActions[eventType]
                    ?.asSequence()
                    ?.filter { it.tagName == localName }
                    ?.forEach { it.action.invoke(this) }
            }
            check(eventType == XMLEvent.END_DOCUMENT)
        }
    }
}

fun Writer.streamXml(): XMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(this)

fun XMLStreamWriter.prettyPrinter(): XMLStreamWriter = IndentingXMLStreamWriter(this)

inline fun XMLStreamWriter.document(
    version: String? = null,
    encoding: String? = null,
    init: XMLStreamWriter.() -> Unit
) = apply {
    when {
        encoding != null && version != null -> writeStartDocument(encoding, version)
        version != null -> writeStartDocument(version)
        else -> writeStartDocument()
    }
    init()
    writeEndDocument()
}

inline fun XMLStreamWriter.tag(
    name: String,
    init: XMLStreamWriter.() -> Unit
) = apply {
    writeStartElement(name)
    init()
    writeEndElement()
}

fun XMLStreamWriter.emptyTag(
    name: String,
    init: (XMLStreamWriter.() -> Unit)? = null
) = apply {
    writeEmptyElement(name)
    init?.invoke(this)
}

inline fun XMLStreamWriter.tag(
    name: String,
    content: String,
    init: XMLStreamWriter.() -> Unit
) = apply {
    tag(name) {
        init()
        writeCharacters(content)
    }
}

fun XMLStreamWriter.tag(name: String, content: String) {
    tag(name) {
        writeCharacters(content)
    }
}

fun XMLStreamWriter.comment(content: String) {
    writeComment(content)
}

fun XMLStreamWriter.attribute(name: String, value: String) = writeAttribute(name, value)

abstract class DelegatingXMLStreamWriter(writer: XMLStreamWriter) : XMLStreamWriter by writer

class IndentingXMLStreamWriter(
    writer: XMLStreamWriter,
    private val indent: String = "  "
) : DelegatingXMLStreamWriter(writer) {

    private var currentState = NOTHING
    private val stateStack = Stack<Any>()

    private var indentationDepth = 0

    private fun onStartTag() {
        stateStack.push(TAG)
        currentState = NOTHING
        writeNL()
        writeIndent()
        indentationDepth++
    }

    private fun onEndTag() {
        indentationDepth--
        if (currentState === TAG) {
            super.writeCharacters("\n")
            writeIndent()
        }
        currentState = stateStack.pop()
    }

    private fun onEmptyTag() {
        currentState = TAG
        writeNL()
        writeIndent()
    }

    private fun writeNL() {
        if (indentationDepth > 0) {
            super.writeCharacters("\n")
        }
    }

    private fun writeIndent() {
        if (indentationDepth > 0) {
            repeat(indentationDepth) {
                super.writeCharacters(indent)
            }
        }
    }

    override fun writeStartDocument() {
        super.writeStartDocument()
        super.writeCharacters("\n")
    }

    override fun writeStartDocument(version: String) {
        super.writeStartDocument(version)
        super.writeCharacters("\n")
    }

    override fun writeStartDocument(encoding: String, version: String) {
        super.writeStartDocument(encoding, version)
        super.writeCharacters("\n")
    }

    override fun writeStartElement(localName: String) {
        onStartTag()
        super.writeStartElement(localName)
    }

    override fun writeStartElement(namespaceURI: String, localName: String) {
        onStartTag()
        super.writeStartElement(namespaceURI, localName)
    }

    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        onStartTag()
        super.writeStartElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        onEmptyTag()
        super.writeEmptyElement(namespaceURI, localName)
    }

    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        onEmptyTag()
        super.writeEmptyElement(prefix, localName, namespaceURI)
    }

    override fun writeEmptyElement(localName: String) {
        onEmptyTag()
        super.writeEmptyElement(localName)
    }

    override fun writeEndElement() {
        onEndTag()
        super.writeEndElement()
    }

    override fun writeCharacters(text: String) {
        currentState = DATA
        super.writeCharacters(text)
    }

    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        currentState = DATA
        super.writeCharacters(text, start, len)
    }

    override fun writeCData(data: String) {
        currentState = DATA
        super.writeCData(data)
    }

    companion object {
        private val NOTHING = Any()
        private val TAG = Any()
        private val DATA = Any()
    }
}
