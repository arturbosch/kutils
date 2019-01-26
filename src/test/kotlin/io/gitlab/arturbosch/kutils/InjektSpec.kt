package io.gitlab.arturbosch.kutils

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.BehaviorSpec
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * @author Artur Bosch
 */
class InjektSpec : BehaviorSpec({

    given("default injektor with some classes") {

        Injekt.addSingleton<PrintStream>(System.out)
        Injekt.addFactory { Logger() }
        Injekt.addSingletonFactory { VisitHandler() }
        Injekt.addFactory { Counter() }

        `when`("using the same singleton in two threads to loop 100x") {
            val thread = thread {
                val handler = Injekt.get<VisitHandler>()
                for (i in 1..100) {
                    handler.visit()
                }
            }
            val thread2 = thread {
                val handler = Injekt.get<VisitHandler>()
                for (i in 1..100) {
                    handler.visit()
                }
            }

            thread.join()
            thread2.join()

            then("the result must be exactly 200") {
                Injekt.get<VisitHandler>().counter.number.get() shouldBe 200
            }
        }
    }

    given("generic classes") {

        `when`("using different types of a generic class") {

            Injekt.addSingleton(Box<Int>("box-of-int"))
            Injekt.addSingleton(IntBox())
            Injekt.addSingleton(Box<String>("box-of-string"))
            Injekt.addSingleton(Box<List<Int>>("box-of-list-of-int"))
            Injekt.addSingleton(Box<List<String>>("box-of-list-of-string"))
            Injekt.addSingleton(Box<Map<String, List<Set<Int>>>>("box-of-map-of-string-and-list-of-set-of-int"))

            then("each individual generic type is returned") {
                val intBox: Box<Int> = Injekt.get()
                val intBox2: IntBox = Injekt.get()
                val stringBox: Box<String> = Injekt.get()
                val string2Box: Box<String> = Injekt.get()
                val listIntBox: Box<List<Int>> = Injekt.get()
                val listStringBox: Box<List<String>> = Injekt.get()
                val mapBox: Box<Map<String, List<Set<Int>>>> = Injekt.get()
                intBox.name shouldBe "box-of-int"
                intBox2.name shouldBe "int-box"
                stringBox.name shouldBe "box-of-string"
                listIntBox.name shouldBe "box-of-list-of-int"
                listStringBox.name shouldBe "box-of-list-of-string"
                listStringBox.name shouldBe "box-of-list-of-string"
                mapBox.name shouldBe "box-of-map-of-string-and-list-of-set-of-int"
                stringBox shouldBe string2Box
            }
        }

        and("unregistered type throw errors") {
            shouldThrow<InvalidDependency> { Injekt.get<Box<Any>>() }
            shouldThrow<InvalidDependency> { Injekt.get<Box<*>>() }
        }
    }

    given("circular dependencies") {

        `when`("retrieving a from b eagerly") {

            Injekt.addSingletonFactory { EagerA() }
            Injekt.addSingletonFactory { EagerB() }

            then("a circular dependency is detected") {
                val error = shouldThrow<CircularDependency> {
                    Injekt.get<EagerA>()
                }
                println(error)
            }
        }

        `when`("retrieving a from b lazily") {

            Injekt.addSingletonFactory { LazyA() }
            Injekt.addSingletonFactory { LazyB() }

            val a = Injekt.get<LazyA>()
            val b = Injekt.get<LazyB>()

            then("it should be lazily created") {
                a shouldBe b.a
                b shouldBe a.b
            }
        }
    }
}) {

    override fun afterTest(description: Description, result: TestResult) {
        Injekt.clearFactories()
    }
}

@Suppress("unused")
open class Box<T : Any>(val name: String)

class IntBox : Box<Int>("int-box")

val Injekt = TestInjektor()

open class TestInjektor : DefaultInjektor() {
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
