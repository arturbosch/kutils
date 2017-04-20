package io.gitlab.arturbosch.kutils

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
class TryTest {

	@Test
	fun tryAsInvoke() {
		Try {
			5
		} then {
			it * it
		} onSuccess {
			assertEquals(it, 25)
		} onError {
			fail()
		}
	}

	@Test
	fun valueAndErrorPresetIsLikeValueIsPresent() {
		val value = Try(5, Throwable("ERROR"))
		val value1 = value then { it * it }
		assertEquals(value1.value, 25)
		assertEquals(value1.error, null)
	}

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

	@Test
	fun tryThenThenCascade() {
		tryTo {
			5
		} then {
			"Number $it"
		} then {
			"$it !!!"
		} onSuccess {
			assertEquals(it, "Number 5 !!!")
		} onError {
			fail()
		}
	}

	@Test
	fun tryWithZip() {
		fun double(x: Int) = Try(x * x, null)
		tryTo {
			5
		} zip ::double then ::double zip {
			it
		} onSuccess {
			assertEquals(it, 625)
		} onError {
			fail()
		}
	}

	@Test
	fun tryWithCompose() {
		tryTo {
			5
		} compose { value, _ ->
			if (value != null) value * value else fail()
		} onSuccess {
			assertEquals(it, 25)
		} onError {
			fail()
		}
	}
}