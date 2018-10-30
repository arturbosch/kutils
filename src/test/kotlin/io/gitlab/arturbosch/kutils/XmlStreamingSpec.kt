package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.CharArrayWriter
import java.io.StringReader
import java.util.ArrayDeque
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

/**
 * @author Artur Bosch
 */
internal class XmlStreamingSpec : StringSpec({

    val expected = """
        <?xml version="1.0" encoding="utf-8"?>
        <!--This is THE architecture!-->
        <Architecture>
          <Module key="value">Content1</Module>
          <Module>Content2</Module>
          <Cycle>
            <Dependency attribute1="value1" attribute2="value2"></Dependency>
            <Dependency attribute1="value1" attribute2="value2"></Dependency>
          </Cycle>
          <Module/>
          <Module key="empty"/>
        </Architecture>
        <!--This is THE end!-->
    """.trimIndent()

    "can write xml" {
        val writer = CharArrayWriter()
        writer.streamXml().prettyPrinter().apply {
            document("1.0", "utf-8") {
                comment("This is THE architecture!")
                writeCharacters("\n")
                tag("Architecture") {
                    tag("Module", "Content1") {
                        attribute("key", "value")
                    }
                    tag("Module", "Content2")
                    tag("Cycle") {
                        tag("Dependency") {
                            attribute("attribute1", "value1")
                            attribute("attribute2", "value2")
                        }
                        tag("Dependency") {
                            attribute("attribute1", "value1")
                            attribute("attribute2", "value2")
                        }
                    }
                    emptyTag("Module")
                    emptyTag("Module") {
                        attribute("key", "empty")
                    }
                }
                writeCharacters("\n")
                comment("This is THE end!")
            }
        }

        val actual = writer.toString()

        actual shouldBe expected
    }

    "can read xml" {
        val dependencies = ArrayDeque<Dependency>()
        val cycles = mutableListOf<Cycle>()
        val modules = mutableListOf<Module>()

        fun parseModule(xml: XMLStreamReader) {
            xml.run {
                var key: String? = null
                for (i in 0..attributeCount) {
                    if (getAttributeLocalName(i) == "key") {
                        key = getAttributeValue(i)
                    }
                }
                val content: String? = elementText
                modules.add(Module(key, content))
            }
        }

        fun parseDependency(xml: XMLStreamReader) {
            xml.apply {
                dependencies.add(Dependency(
                        getAttributeValue(null, "attribute1"),
                        getAttributeValue(null, "attribute2")
                ))
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun parseCycle(xml: XMLStreamReader) {
            cycles.add(Cycle(dependencies.pop(), dependencies.pop()))
        }

        val endings = mutableListOf<String>()

        val architecture = StringReader(expected).streamXml<Architecture> {
            onTag("Module", ::parseModule)
            onTagEnd("Cycle", ::parseCycle)
            onTag("Dependency", ::parseDependency)
            onAny(XMLEvent.END_ELEMENT) { endings.add(localName) }
            onFinish { Architecture(modules, cycles) }
        }

        architecture.cycles should haveSize(1)
        architecture.modules should haveSize(4)
        endings should haveSize(4) // all module contents are consumed for are end tags
    }
})

data class Module(val key: String? = null, val content: String? = null)
data class Dependency(val attribute1: String, val attribute2: String)
data class Cycle(val left: Dependency, val right: Dependency)
data class Architecture(val modules: List<Module>, val cycles: List<Cycle>)
