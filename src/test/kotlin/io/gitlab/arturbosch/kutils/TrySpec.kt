package io.gitlab.arturbosch.kutils

import io.kotlintest.specs.StringSpec
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
class TrySpec : StringSpec({

    "it should be able to use the Try invoke function" {
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

    "only result or error are allowed" {
        assertFails { Try(5, Throwable("ERROR")) }
        assertFails { Try(null, null) }
        assertNotNull(Try(5, null))
        assertNotNull(Try(null, Throwable("ERROR")))
    }

    "it should not fail with error" {
        tryTo {
            "Hello World"
        } onError {
            fail()
        }
    }

    "it should fail with error" {
        tryTo {
            throw IllegalStateException()
        } onSuccess {
            fail()
        }
    }

    "null as result throws illegal argument exception" {
        tryTo {
            null
        } onSuccess {
            fail()
        } onError {
            assertTrue { it is IllegalArgumentException }
        }
    }

    "try then-cascades are possible" {
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

    "zip function should make one try out of two" {
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

    "compose can be used to test both the result and exception states" {
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
})
