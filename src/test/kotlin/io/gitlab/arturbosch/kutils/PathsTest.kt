package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.specs.StringSpec

internal class PathsTest : StringSpec({

    "can parse tilde (~)" {
        val path = path("~/Files")
        path.toString() shouldNotContain "~"
    }
})
