package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.CharArrayWriter

/**
 * @author Artur Bosch
 */
internal class IndentingXMLStreamWriterTest : StringSpec({

    val expected = """
        <?xml version="1.0" encoding="utf-8"?>
        <!--This is THE architecture!-->
        <Architecture>
          <Module1 key="value">Content1</Module1>
          <Module2>Content2</Module2>
          <Cycle>
            <Dependency attribute1="value1" attribute2="value2"></Dependency>
            <Dependency attribute1="value1" attribute2="value2"></Dependency>
          </Cycle>
          <Empty/>
          <Name empty="true"/>
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
                    tag("Module1", "Content1") {
                        attribute("key", "value")
                    }
                    tag("Module2", "Content2")
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
                    emptyTag("Empty")
                    emptyTag("Name") {
                        attribute("empty", "true")
                    }
                }
                writeCharacters("\n")
                comment("This is THE end!")
            }
        }

        val actual = writer.toString()

        actual shouldBe expected
    }
})
