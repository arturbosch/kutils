package io.gitlab.arturbosch.kutils

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class TimingTest {

	@Test
	fun timing() {
		val (time, result) = measureAndReturn { "Hi" }

		assertTrue(time >= 0)
		assertEquals(result, "Hi")
	}
}
