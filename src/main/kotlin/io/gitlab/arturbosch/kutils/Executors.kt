package io.gitlab.arturbosch.kutils

import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

val cores get() = Runtime.getRuntime().availableProcessors()

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
) = ThreadPoolExecutor(coreThreads, maxThreads,
    0L, TimeUnit.MILLISECONDS,
    LinkedBlockingQueue<Runnable>(),
    threadFactory,
    ThreadPoolExecutor.AbortPolicy())

fun withNamedThreadPoolExecutor(
    name: String,
    coreThreads: Int = cores,
    maxThreads: Int = coreThreads
) = withCustomThreadFactoryExecutor(
    PrefixedThreadFactory(name), coreThreads, maxThreads)

open class PrefixedThreadFactory(private val namePrefix: String) : ThreadFactory {
    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val name: String

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        val pool = poolNumber.andIncrement
        name = namePrefix + (if (pool == 1) "" else "-$pool") + "-worker-"
    }

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(
            group,
            r,
            namePrefix + threadNumber.andIncrement,
            0
        )
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        return thread
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }
}
