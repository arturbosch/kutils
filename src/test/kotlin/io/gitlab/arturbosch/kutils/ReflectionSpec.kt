package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class ReflectionSpec : StringSpec({

    "should return fully qualified class names" {
        className<String>() shouldBe "java.lang.String"
        className<ReflectionTest>() shouldBe "io.gitlab.arturbosch.kutils.ReflectionTest"
        className<ReflectionTest>() shouldBe ReflectionTest::class.java.name
    }
})

class ReflectionTest
