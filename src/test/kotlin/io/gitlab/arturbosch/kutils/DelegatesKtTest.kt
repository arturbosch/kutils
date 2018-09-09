package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

/**
 * @author Artur Bosch
 */
internal class DelegatesTest : StringSpec({

	class A {
		var a: String by single()
		val logger by julLogger()
	}

	"should allow only single assignment" {
		val a = A()
		a.a = "hi"
		shouldThrow<IllegalStateException> { a.a = "bye";a.a }
	}

	"should provide a logger" {
		val a = A()
		a.logger shouldNotBe null
	}
})
