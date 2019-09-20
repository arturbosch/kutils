package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ConsoleTreesSpec : StringSpec({

    "simple testcase" {
        val root = Node(
            "A",
            listOf(
                Node("B", listOf(Node("C", listOf()))),
                Node("D", listOf())
            )
        )

        val content = toTreeString(root)

        content.trimIndent() shouldBe """
             ── A
                ├── B
                │   └── C
                └── D
        """.trimIndent()
    }

    "path based testcase" {
        val content = toTreeString(
            resourceAsPath("consoleTree"),
            DefaultConsoleTreeConverters.PathConverter()
        )

        content.trimIndent() shouldBe """
            ── consoleTree
               ├── A
               │   ├── file1
               │   ├── file4
               │   └── C
               │       └── file2
               └── B
                   └── file3
        """.trimIndent()
    }
})

class Node(
    override val label: String,
    override val children: List<ConsoleTreeNode>
) : ConsoleTreeNode

