package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

class ScannerSpec {

    private val testcase1 = """
        Hello World!

        // comment
        I hope you read this?

        Do you?

        ... // second comment

        \n
        """.trimIndent()

    @Test
    fun `scans content`() {
        tokenize(testcase1).joinToString(" ") { it.content } shouldBe
            "Hello World! // comment I hope you read this? Do you? ... // second comment \\n"
    }

    @Test
    fun `counts lines`() {
        tokenize(testcase1).last().line shouldBe 10
    }

    @Test
    fun `allows to configure a line matcher to match comments`() {
        val matcher = Regex(".*//.*")
        var comments = 0

        tokenize(testcase1) { line ->
            if (line.matches(matcher)) {
                comments++
            }
        }.consume()

        comments shouldBe 2
    }

    @Test
    fun `it can be used to calculate blank lines`() {
        var blanks = 0

        tokenize(testcase1) { line ->
            if (line.isEmpty()) {
                blanks++
            }
        }.consume()

        blanks shouldBe 4
    }

    @Test
    fun `can be used to count lines with comments`() {
        val matcher = Regex("(?<before>.*)(?<comment>//.*)")
        var linesWithComments = 0

        tokenize(testcase1) { line ->
            val matchGroups = matcher.matchEntire(line)?.groups
            if (matchGroups != null) {
                if (matchGroups["before"]?.value?.trim()?.isNotEmpty() == true) {
                    linesWithComments++
                }
            }
        }.consume()

        linesWithComments shouldBe 1
    }
}
