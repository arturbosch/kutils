package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class StreamsKtSpec : StringSpec({

    "stream into list" {
        arrayOf(1, 2, 3)
            .stream()
            .toList() shouldBe listOf(1, 2, 3)
    }

    "stream into set" {
        arrayOf(1, 2, 3)
            .stream()
            .toSet() shouldBe setOf(1, 2, 3)
    }

    "stream into custom collection" {
        arrayOf(1, 2, 3)
            .stream()
            .into { LinkedHashSet() } shouldBe linkedSetOf(1, 2, 3)
    }
})
