@file:Suppress("BlockingMethodInNonBlockingContext")

package io.gitlab.arturbosch.kutils

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * @author Artur Bosch
 */
class ContainerSpec : StringSpec({

    """
        given: default container with some classes
        when: using the same singleton in two threads to loop 100x
        then: the result must be exactly 200
    """ {

        Injekt.addSingleton<PrintStream>(System.out)
        Injekt.addFactory { Logger() }
        Injekt.addSingletonFactory { VisitHandler() }
        Injekt.addFactory { Counter() }

        val thread = thread {
            val handler = Injekt.get<VisitHandler>()
            for (ignored in 1..100) {
                handler.visit()
            }
        }
        val thread2 = thread {
            val handler = Injekt.get<VisitHandler>()
            for (ignored in 1..100) {
                handler.visit()
            }
        }

        thread.join()
        thread2.join()

        Injekt.get<VisitHandler>().counter.number.get() shouldBe 200
    }

    """
        given: generic classes
        when: using different types of a generic class
        then: each individual generic type is returned
        and: unregistered type throw errors
    """ {

        Injekt.addSingleton(Box<Int>("box-of-int"))
        val withIntBox = Injekt.withSingleton(IntBox())
        Injekt.addSingleton(Box<String>("box-of-string"))
        Injekt.addSingleton(Box<List<Int>>("box-of-list-of-int"))
        Injekt.addSingleton(Box<List<String>>("box-of-list-of-string"))
        Injekt.addSingleton(Box<Map<String, List<Set<Int>>>>("box-of-map-of-string-and-list-of-set-of-int"))

        val intBox: Box<Int> = Injekt.get()
        val intBox2: IntBox = Injekt.get()
        val stringBox: Box<String> = Injekt.get()
        val string2Box: Box<String> = Injekt.get()
        val listIntBox: Box<List<Int>> = Injekt.get()
        val listStringBox: Box<List<String>> = Injekt.get()
        val mapBox: Box<Map<String, List<Set<Int>>>> = Injekt.get()
        intBox.name shouldBe "box-of-int"
        intBox2.name shouldBe "int-box"
        intBox2 shouldBe withIntBox
        stringBox.name shouldBe "box-of-string"
        listIntBox.name shouldBe "box-of-list-of-int"
        listStringBox.name shouldBe "box-of-list-of-string"
        listStringBox.name shouldBe "box-of-list-of-string"
        mapBox.name shouldBe "box-of-map-of-string-and-list-of-set-of-int"
        stringBox shouldBe string2Box

        shouldThrow<InvalidDependency> { Injekt.get<Box<Any>>() }
        shouldThrow<InvalidDependency> { Injekt.get<Box<*>>() }
    }

    """
        given: circular dependencies
        when: retrieving a from b eagerly
        then: a circular dependency is detected
    """ {

        Injekt.addSingletonFactory { EagerA() }
        Injekt.addSingletonFactory { EagerB() }

        val error = shouldThrow<CircularDependency> {
            Injekt.get<EagerA>()
        }
        println(error)

        Injekt.addSingletonFactory { LazyA() }
        Injekt.addSingletonFactory { LazyB() }

        val a = Injekt.get<LazyA>()
        val b = Injekt.get<LazyB>()

        a shouldBe b.a
        b shouldBe a.b
    }

    "lazy injection with initialize function" {
        Injekt.addSingletonFactory { Counter() }
        Injekt.addSingletonFactory { LazyWithInitBox() }

        val counter = Injekt.get<LazyWithInitBox>().counter

        counter.number.get() shouldBe 1
    }
}) {

    override fun afterTest(testCase: TestCase, result: TestResult) {
        Injekt.clearFactories()
    }
}

@Suppress("unused")
open class Box<T : Any>(val name: String)

class IntBox : Box<Int>("int-box")

val Injekt = TestContainer()

open class TestContainer : DefaultContainer() {
    fun clearFactories(): Unit = factories.clear()
}

class VisitHandler(
    val counter: Counter = Injekt.get(),
    private val logger: Logger = Injekt.get()
) {
    fun visit() {
        counter.inc()
        logger.log("Hello no. ${counter.number}")
    }
}

class Counter {
    var number = AtomicInteger(0)
    fun inc() = number.incrementAndGet()
}

data class Logger(private val out: PrintStream = Injekt.get()) {
    fun log(msg: String) {
        out.println(msg)
    }
}

class EagerA(@Suppress("unused") val b: EagerB = Injekt.get())
class EagerB(@Suppress("unused") val a: EagerA = Injekt.get())

class LazyA {
    val b: LazyB by Injekt.lazy()
}

class LazyB {
    val a: LazyA by Injekt.lazy()
}

class LazyWithInitBox {
    val counter: Counter by Injekt.lazy { it.inc() }
}
