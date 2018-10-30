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
        require(eventType in 1..15)
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
        init: XMLStreamWriter.() -> Unit) = apply {
    writeStartElement(name)
    init()
    writeEndElement()
}

fun XMLStreamWriter.emptyTag(
        name: String,
        init: (XMLStreamWriter.() -> Unit)? = null) = apply {
    writeEmptyElement(name)
    init?.invoke(this)
}

inline fun XMLStreamWriter.tag(
        name: String,
        content: String,
        init: XMLStreamWriter.() -> Unit) = apply {
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

    private var state = SEEN_NOTHING
    private val stateStack = Stack<Any>()

    private var depth = 0

    private fun onStartElement() {
        stateStack.push(SEEN_ELEMENT)
        state = SEEN_NOTHING
        writeNL()
        writeIndent()
        depth++
    }

    private fun onEndElement() {
        depth--
        if (state === SEEN_ELEMENT) {
            super.writeCharacters("\n")
            writeIndent()
        }
        state = stateStack.pop()
    }

    private fun onEmptyElement() {
        state = SEEN_ELEMENT
        writeNL()
        writeIndent()
    }

    private fun writeNL() {
        if (depth > 0) {
            super.writeCharacters("\n")
        }
    }

    private fun writeIndent() {
        if (depth > 0) {
            for (i in 0 until depth)
                super.writeCharacters(indent)
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
        onStartElement()
        super.writeStartElement(localName)
    }

    override fun writeStartElement(namespaceURI: String, localName: String) {
        onStartElement()
        super.writeStartElement(namespaceURI, localName)
    }


    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        onStartElement()
        super.writeStartElement(prefix, localName, namespaceURI)
    }


    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        onEmptyElement()
        super.writeEmptyElement(namespaceURI, localName)
    }


    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        onEmptyElement()
        super.writeEmptyElement(prefix, localName, namespaceURI)
    }


    override fun writeEmptyElement(localName: String) {
        onEmptyElement()
        super.writeEmptyElement(localName)
    }


    override fun writeEndElement() {
        onEndElement()
        super.writeEndElement()
    }


    override fun writeCharacters(text: String) {
        state = SEEN_DATA
        super.writeCharacters(text)
    }


    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        state = SEEN_DATA
        super.writeCharacters(text, start, len)
    }


    override fun writeCData(data: String) {
        state = SEEN_DATA
        super.writeCData(data)
    }

    companion object {
        private val SEEN_NOTHING = Any()
        private val SEEN_ELEMENT = Any()
        private val SEEN_DATA = Any()
    }
}
