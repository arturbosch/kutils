package io.gitlab.arturbosch.kutils

import java.io.Writer
import java.util.Stack
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

fun Writer.streamXml(): XMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(this)

fun XMLStreamWriter.prettyPrinter(): XMLStreamWriter = IndentingXMLStreamWriter(this)

inline fun XMLStreamWriter.document(
        version: String? = null,
        encoding: String? = null,
        init: XMLStreamWriter.() -> Unit
) = apply {
    if (encoding != null && version != null) {
        writeStartDocument(encoding, version)
    } else if (version != null) {
        writeStartDocument(version)
    } else {
        writeStartDocument()
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
