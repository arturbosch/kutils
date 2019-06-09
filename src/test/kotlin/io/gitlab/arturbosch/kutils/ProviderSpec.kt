package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ProviderSpec : StringSpec({

    "provides strings" {
        val provider = object : Provider<String> {
            override fun provide(container: Container): String {
                return "Hello"
            }
        }

        provider.provide(DefaultContainer()) shouldBe "Hello"
    }
})
