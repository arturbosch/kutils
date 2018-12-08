package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.InputStream
import java.net.URI

/**
 * @author Artur Bosch
 */
internal class ResourcesSpec : StringSpec({

    "can load resource as URI" {
        resource("test") should beInstanceOf<URI>()
    }

    "can load resource as stream" {
        resourceAsStream("test").use {
            it should beInstanceOf<InputStream>()
        }
    }

    "can load resource into string" {
        resourceAsString("test").trim() shouldBe "test"
    }
})
