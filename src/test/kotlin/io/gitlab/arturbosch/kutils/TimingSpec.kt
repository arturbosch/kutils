package io.gitlab.arturbosch.kutils

import io.kotlintest.specs.StringSpec
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class TimingSpec : StringSpec({

    "it should return the time in long plus the result" {
        val (time, result) = measureAndReturn { "Hi" }

        assertTrue(time >= 0)
        assertEquals(result, "Hi")
    }
})
