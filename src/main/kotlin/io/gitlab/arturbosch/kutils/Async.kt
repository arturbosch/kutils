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
inline fun <T> withExecutor(executor: ExecutorService = Executors.newFixedThreadPool(cores),
							block: ExecutorService.() -> T) = block.invoke(executor).apply {
	executor.shutdown()
}

/**
 * Starts given block in a completable future with this executor.
 */
inline fun <T> Executor.runAsync(crossinline block: () -> T) = task(this) { block() }

/**
 * Starts given task as a completable future. If no executor is specialized, the common
 * thread pool is used for this.
 */
inline fun <T> task(executor: Executor = ForkJoinPool.commonPool(), crossinline task: () -> T): CompletableFuture<T> = CompletableFuture.supplyAsync(Supplier { task() }, executor)

/**
 * Awaits the execution of all given completable futures. Returns the results of the futures.
 */
fun <T> awaitAll(futures: List<CompletableFuture<T>>): List<T> {
	CompletableFuture.allOf(*futures.toTypedArray()).join()
	return futures.map { it.get() }
}

/**
 * Awaits the execution of all given completable futures. Returns the results of the futures.
 */
fun <T> awaitAll(vararg futures: CompletableFuture<T>): List<T> {
	CompletableFuture.allOf(*futures).join()
	return futures.map { it.get() }
}