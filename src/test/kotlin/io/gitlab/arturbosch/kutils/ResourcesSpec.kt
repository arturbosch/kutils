package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.InputStream
import java.net.URI
import java.net.URL

/**
 * @author Artur Bosch
 */
internal class ResourcesSpec : StringSpec({

    "can load resource as URI" {
        resourceAsUri("test") should beInstanceOf<URI>()
    }

    "can load resource as URL" {
        resource("test") should beInstanceOf<URL>()
    }

    "can load resource as stream" {
        resourceAsStream("test").use {
            it should beInstanceOf<InputStream>()
        }
    }

    "can load resource into string" {
        resourceAsText("test").trim() shouldBe "test"
    }
})
