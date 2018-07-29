package io.gitlab.arturbosch.kutils

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
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
}) {

	override fun afterTest(description: Description, result: TestResult) {
		Injekt.clearFactories()
	}
}

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
