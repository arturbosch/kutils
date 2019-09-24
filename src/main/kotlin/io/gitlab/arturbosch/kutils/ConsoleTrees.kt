package io.gitlab.arturbosch.kutils

import java.nio.file.Path

interface ConsoleTreeNode {

    val label: String
    val children: List<ConsoleTreeNode>
}

interface ConsoleTreeConverter<T> {

    fun label(node: T): String
    fun children(node: T): List<T>
}

fun toTreeString(startNode: ConsoleTreeNode): String = toTreeString(
    startNode,
    object : ConsoleTreeConverter<ConsoleTreeNode> {
        override fun label(node: ConsoleTreeNode): String = node.label
        override fun children(node: ConsoleTreeNode): List<ConsoleTreeNode> = node.children
    }
)


fun <T> toTreeString(startNode: T, converter: ConsoleTreeConverter<T>): String {
    val result = StringBuilder()

    fun process(
        current: T,
        indent: String,
        isRoot: Boolean = false,
        isLast: Boolean = false
    ) {
        val prefix = if (isRoot) {
            " ── "
        } else {
            if (isLast) "└── " else "├── "
        }

        result.append(indent)
            .append(prefix)
            .append(converter.label(current))
            .append("\n")

        for ((index, child) in converter.children(current).withIndex()) {
            process(
                child,
                indent = indent + if (isLast || isRoot) "    " else "│   ",
                isLast = converter.children(current).size - 1 == index
            )
        }
    }

    process(startNode, "", isRoot = true)

    return result.toString()
}

@Suppress("FunctionName")
object DefaultConsoleTreeConverters {
    fun PathConverter() = object : ConsoleTreeConverter<Path> {
        override fun label(node: Path): String {
            return node.fileName.toString()
        }

        override fun children(node: Path): List<Path> {
            if (node.isDirectory()) {
                return node.list().sortedBy { it.fileName.toString() }.toList()
            }
            return emptyList()
        }
    }
}
