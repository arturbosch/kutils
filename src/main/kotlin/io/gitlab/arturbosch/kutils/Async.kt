package io.gitlab.arturbosch.kutils

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.function.Supplier

/**
 * Creates a new fixed thread pool with #cores threads and allows to execute commands on the
 * created execution service. The executor will automatically be closed.
 */

fun <T> withExecutor(block: ExecutorService.() -> T): T {
	val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
	return block.invoke(executor).apply {
		executor.shutdown()
	}
}

/**
 * Starts given block in a completable future with this executor.
 */
fun <T> Executor.runAsync(block: () -> T): CompletableFuture<T> {
	return task(this) { block() }
}

/**
 * Starts given task as a completable future. If no executor is specialized, the common
 * thread pool is used for this.
 */
fun <T> task(executor: Executor = ForkJoinPool.commonPool(), task: () -> T): CompletableFuture<T> {
	return CompletableFuture.supplyAsync(Supplier { task() }, executor)
}

/**
 * Awaits the execution of all given completable futures. Returns the results of the futures.
 */
fun <T> awaitAll(futures: List<CompletableFuture<T>>): List<T> {
	CompletableFuture.allOf(*futures.toTypedArray()).join()
	return futures.map { it.get() }
}