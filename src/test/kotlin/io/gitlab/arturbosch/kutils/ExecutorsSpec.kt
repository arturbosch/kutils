package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.startWith
import io.kotlintest.should
import io.kotlintest.specs.StringSpec

class ExecutorsSpec : StringSpec({

    "threads start with given name" {
        val executor = withNamedThreadPoolExecutor("kutils")

        var threadName: String? = null
        executor.submit { threadName = Thread.currentThread().name }
        executor.shutdown()

        threadName.should { startWith("kutils") }
    }
})
