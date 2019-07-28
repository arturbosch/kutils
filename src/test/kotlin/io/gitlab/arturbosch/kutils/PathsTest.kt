package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.specs.StringSpec

internal class PathsTest : StringSpec({

    "can parse tilde (~)" {
        val path = Path("~/Files")
        path.toString() shouldNotContain "~"
    }
})
