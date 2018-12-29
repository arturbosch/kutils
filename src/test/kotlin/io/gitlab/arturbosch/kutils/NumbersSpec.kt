package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class NumbersSpec : StringSpec({

    "positive" {
        (-1).positive() shouldBe 0
        0.positive() shouldBe 0
        100.positive() shouldBe 100
    }

    "positive doubles" {
        (-1.0).positive() shouldBe 0.0
        0.0.positive() shouldBe 0.0
        100.0.positive() shouldBe 100.0
    }
})
