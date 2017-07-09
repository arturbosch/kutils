package io.gitlab.arturbosch.kutils.dependency

import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */

fun main(args: Array<String>) {
	val graph = buildObjectGraph()
	val thread = thread {
		measureTimeMillis {
			val handler = graph.get(VisitHandler::class.java)
			val counter = graph.get<Counter>()
			handler.visit()
			println(counter.counter)
		}.apply { println("Millis needed: " + this) }
	}
	val thread2 = thread {
		measureTimeMillis {
			val handler = graph.get(VisitHandler::class.java)
			val counter = graph.get<Counter>()
			handler.visit()
			println(counter.counter)
		}.apply { println("Millis needed: " + this) }
	}

	thread.join()
	thread2.join()
}

fun buildObjectGraph(): ObjectGraph {
	val linker = Linker(true)
	linker.install<PrintStream>(ValueFactory.of(System.out))
	return ObjectGraph(linker)
}

class VisitHandler @Inject constructor(val counter: Counter, val logger: Logger) {
	fun visit() {
		counter.inc()
		logger.log("Hello no. ${counter.counter}")
	}
}

@Singleton
class Counter {
	var counter = AtomicInteger(0)
	fun inc() = counter.incrementAndGet()
}

data class Logger @Inject constructor(val out: PrintStream) {
	fun log(msg: String) {
		out.println(msg)
	}
}
