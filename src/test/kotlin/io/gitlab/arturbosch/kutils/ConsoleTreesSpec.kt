package io.gitlab.arturbosch.kutils

import io.kotlintest.specs.StringSpec

class ConsoleTreesSpec : StringSpec({

    class Node(
        override val label: String,
        override val children: List<ConsoleTreeNode>
    ) : ConsoleTreeNode

    fun assertLinesAreEqual(actual: String, expected: String) {
        actual.trimIndent().split(System.lineSeparator())
            .zip(expected.trimIndent().split(System.lineSeparator()))
            .map { it.first.trim() to it.second.trim() }
            .forEach { it.first == it.second }
    }

    "simple testcase" {
        assertLinesAreEqual(
            toTreeString(Node(
                "A",
                listOf(
                    Node("B", listOf(Node("C", listOf()))),
                    Node("D", listOf())
                )
            )),
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
               │   ├── file1
               │   ├── file4
               │   └── C
               │       └── file2
               └── B
                   └── file3
            """
        )
    }
})
