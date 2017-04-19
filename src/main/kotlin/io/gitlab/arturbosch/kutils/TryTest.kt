package io.gitlab.arturbosch.kutils

import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
class TryTest {

	@Test
	fun tryOnSuccess() {
		tryTo {
			"Hello World"
		} onError {
			fail()
		}
	}

	@Test
	fun tryOnError() {
		tryTo {
			throw Throwable()
		} onSuccess {
			fail()
		}
	}

	@Test
	fun valueOfNullResultsInError() {
		tryTo {
			null
		} onSuccess {
			fail()
		} onError {
			assertTrue { it is IllegalArgumentException }
		}
	}
}