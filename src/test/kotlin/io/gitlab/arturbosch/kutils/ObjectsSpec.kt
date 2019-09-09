package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ObjectsSpec : StringSpec({

    "transforms if condition is true" {
        1.transformIf(true) { it * 2 } shouldBe 2
        1.transformIf({ it == 1 }) { it * 2 } shouldBe 2
        1.transformIf(false) { it * 2 } shouldBe 1
        1.transformIf({ it == 2 }) { it * 2 } shouldBe 1
    }
})
