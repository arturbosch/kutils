package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.beOfType
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.specs.StringSpec

internal class ServiceLoadersTest : StringSpec({

    "can load prioritized services" {
        val loader = load<Service>()
        val all = loader.toList()

        all should haveSize(2)

        loader.reload()
        val service = loader.firstPrioritized()
        service should beOfType<BService>()
    }
})

interface Service : WithPriority

class AService : Service {
    override val priority: Int = 2
}

class BService : Service {
    override val priority: Int = Int.MAX_VALUE
}
