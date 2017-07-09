package io.gitlab.arturbosch.kutils.dependency

import org.junit.Test
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.asserter

/**
 * @author Artur Bosch
 */
class ObjectGraphTest {

	@Test
	fun dependencyGraph() {
		val linker = Linker(true)
		linker.install<PrintStream>(ValueFactory.of(System.out))
		val graph = ObjectGraph(linker)
		val hOne = graph.get(VisitHandler::class.java)
		val hTwo = graph.get(VisitHandler::class.java)
		hOne.visit()

		asserter.assertEquals("Counter must be a singleton",
				hOne.counter.number, hTwo.counter.number)
	}

	@Test
	fun threadSafeGraph() {
		val linker = Linker(true)
		linker.install<PrintStream>(ValueFactory.of(System.out))
		val graph = ObjectGraph(linker)

		val thread = thread {
			val handler = graph.get(VisitHandler::class.java)
			for (i in 1..100) {
				handler.visit()
			}
		}
		val thread2 = thread {
			val handler = graph.get(VisitHandler::class.java)
			for (i in 1..100) {
				handler.visit()
			}
		}

		thread.join()
		thread2.join()

		val counter = graph.get<Counter>()

		asserter.assertEquals("Linker must be thread safe", 200, counter.number.toInt())
	}
}

class VisitHandler @Inject constructor(val counter: Counter, val logger: Logger) {
	fun visit() {
		counter.inc()
		logger.log("Hello no. ${counter.number}")
	}
}

@Singleton
class Counter {
	var number = AtomicInteger(0)
	fun inc() = number.incrementAndGet()
}

data class Logger @Inject constructor(val out: PrintStream) {
	fun log(msg: String) {
		out.println(msg)
	}
}
