@file:Suppress("MatchingDeclarationName")

package io.gitlab.arturbosch.kutils

import java.util.Scanner

data class Token(val content: String, val index: Int, val line: Int)

fun tokenize(
    content: String,
    lineMatcher: ((String) -> Unit)? = null
): Sequence<Token> = sequence {
    var tokenIndex = 0
    var lineIndex = 0
    Scanner(content).use { contentScanner ->
        while (contentScanner.hasNextLine()) {
            val line = contentScanner.nextLine()
            lineIndex++
            lineMatcher?.invoke(line)
            Scanner(line).use { lineScanner ->
                while (lineScanner.hasNext()) {
                    yield(Token(lineScanner.next(), tokenIndex++, lineIndex))
                }
            }
        }
    }
}
