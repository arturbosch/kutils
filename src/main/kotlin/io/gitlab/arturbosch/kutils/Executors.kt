package io.gitlab.arturbosch.kutils

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

val cores: Int get() = Runtime.getRuntime().availableProcessors()

/**
 * Directly executes the given runnable task on current thread.
 */
class DirectExecutor : Executor {

    override fun execute(command: Runnable) {
        command.run()
    }
}

/**
 * Allows to create a thread pool executors with customized thread factory.
 */
fun withCustomThreadFactoryExecutor(
    threadFactory: ThreadFactory,
    coreThreads: Int = cores,
    maxThreads: Int = coreThreads
): ThreadPoolExecutor = ThreadPoolExecutor(
    coreThreads, maxThreads,
    0L, TimeUnit.MILLISECONDS,
    LinkedBlockingQueue(),
    threadFactory,
    ThreadPoolExecutor.AbortPolicy()
)

fun withNamedThreadPoolExecutor(
    name: String,
    coreThreads: Int = cores,
    maxThreads: Int = coreThreads
): ThreadPoolExecutor = withCustomThreadFactoryExecutor(
    NamedThreadFactory(name),
    coreThreads,
    maxThreads
)

open class NamedThreadFactory(
    val name: String,
    private val backingThreadFactory: ThreadFactory = Executors.defaultThreadFactory()
) : ThreadFactory {

    private val threadNumber = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        val thread = backingThreadFactory.newThread(r)
        thread.name = "$name-worker-${threadNumber.getAndIncrement()}"
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        return thread
    }
}
