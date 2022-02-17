package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ConsoleTreesSpec : StringSpec({

    fun assertLinesAreEqual(actual: String, expected: String) {
        actual.trimIndent().split(System.lineSeparator())
            .zip(expected.trimIndent().split(System.lineSeparator()))
            .map { it.first.trim() to it.second.trim() }
            .forEach { it.first shouldBe it.second }
    }

    "simple testcase" {
        assertLinesAreEqual(
            toTreeString(
                SimpleStringNode(
                    "A",
                    listOf(
                        SimpleStringNode("B", listOf(SimpleStringNode("C", listOf()))),
                        SimpleStringNode("D", listOf())
                    )
                )
            ),
            """
             ── A
                ├── B
                │   └── C
                └── D
            """
        )
    }

    "path based tree" {
        assertLinesAreEqual(
            toTreeString(
                resourceAsPath("consoleTree"),
                DefaultConsoleTreeConverters.PathConverter()
            ),
            """
             ── consoleTree
               ├── A
               │   ├── C
               │   │   └── file2
               │   ├── file1
               │   └── file4
               └── B
                   └── file3
            """
        )
    }
})
