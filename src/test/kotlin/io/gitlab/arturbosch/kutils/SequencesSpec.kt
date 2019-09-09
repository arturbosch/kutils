package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.sequences.shouldContainAll
import io.kotlintest.specs.StringSpec

class SequencesSpec : StringSpec({

    "maps only if condition is true" {
        sequenceOf(1, 2, 3).mapIf(true) { it * 2 } shouldContainAll listOf(2, 4, 6)
        sequenceOf(1, 2, 3).mapIf(false) { it * 2 } shouldContainAll listOf(1, 2, 3)
    }

    "maps only if condition is true via closure" {
        sequenceOf(1, 2, 3).mapIf({ it.sum() == 6 }) { it * 2 } shouldContainAll listOf(2, 4, 6)
        sequenceOf(1, 2, 3).mapIf({ it.sum() == 10 }) { it * 2 } shouldContainAll listOf(1, 2, 3)
    }
})
