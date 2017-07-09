package io.gitlab.arturbosch.kutils.di

import java.io.PrintStream
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 */

fun main(args: Array<String>) {
	measureTimeMillis {
		val graph = buildObjectGraph()
		val handler = graph.get(VisitHandler::class.java)
		val handler2 = graph.get(VisitHandler::class.java)
		val counter = graph.get<Counter>()
		handler.visit()
		handler2.visit()
		println(counter.counter)
	}.apply { println("Millis needed: " + this) }

}

fun buildObjectGraph(): ObjectGraph {
	val linker = Linker()
	installFactories(linker)
	return ObjectGraph(linker)
}

fun installFactories(linker: Linker) {
	linker.install<PrintStream>(ValueFactory.of(System.out))
}

class VisitHandler @Inject constructor(val counter: Counter, val logger: Logger) {
	fun visit() {
		counter.inc()
		logger.log("Hello no. ${counter.counter}")
	}
}

@Singleton
class Counter {
	var counter: Int = 0
	fun inc() {
		counter++
	}
}

data class Logger @Inject constructor(val out: PrintStream) {
	fun log(msg: String) {
		out.println(msg)
	}
}
