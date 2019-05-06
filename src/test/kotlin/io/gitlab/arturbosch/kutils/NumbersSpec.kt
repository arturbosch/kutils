package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class NumbersSpec : StringSpec({

    "positive ints" {
        (-1).positive() shouldBe 0
        0.positive() shouldBe 0
        100.positive() shouldBe 100
    }

    "positive longs" {
        (-1L).positive() shouldBe 0L
        0L.positive() shouldBe 0L
        100L.positive() shouldBe 100L
    }

    "positive floats" {
        (-1.0f).positive() shouldBe 0.0f
        0.0f.positive() shouldBe 0.0f
        100.0f.positive() shouldBe 100.0f
    }

    "positive doubles" {
        (-1.0).positive() shouldBe 0.0
        0.0.positive() shouldBe 0.0
        100.0.positive() shouldBe 100.0
    }
})
