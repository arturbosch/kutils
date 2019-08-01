package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

internal class PathsTest : StringSpec({

    "can parse tilde (~)" {
        val path = Path("~/Files")
        path.toString() shouldNotContain "~"
    }

    "get non empty not commented lines" {
        val path = resourceAsPath("baseDir/test.properties")
        var result: List<String>? = null
        path.useNormalizedLines("#") {
            result = it.toList()
        }

        result shouldNotBe null
        result!! shouldContainAll listOf("key=value", "key2=value")
    }
})
