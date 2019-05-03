@file:Suppress("UNCHECKED_CAST")

package io.gitlab.arturbosch.kutils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Executor
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * An EventBus allows to subscribe to events and subscribers notified whenever given event type
 * gets fired.
 */
interface EventBus {
    /**
     * A subscriber tells the bus on which event types the given action should be called.
     */
    fun <T : Any> subscribe(subscriber: Any, eventType: KClass<T>, action: (T) -> Unit): EventBus

    /**
     * Unsubscribe from given event type for given subscriber.
     */
    fun <T : Any> unsubscribe(subscriber: Any, eventType: KClass<T>): EventBus

    /**
     * Broadcast given event to all subscribers to this event type.
     * ATTENTION: The event continues on the same Thread.
     */
    fun <T : Any> post(event: T)

    /**
     * Broadcast given event to all subscribers to this event type in an asynchronous way.
     * ATTENTION: It can be implementation/usage specific if this is really asynchronous.
     */
    fun <T : Any> postAsync(event: T)

}

inline fun <reified T : Any> EventBus.subscribe(subscriber: Any, noinline action: (T) -> Unit) =
        subscribe(subscriber, T::class, action)

inline fun <reified T : Any> EventBus.unsubscribe(subscriber: Any) =
        unsubscribe(subscriber, T::class)

/**
 * A subscription bundles the action to take when specific event type is triggered for a subscriber.
 */
interface Subscription {
    val eventType: KClass<*>
    val owner: Any
    val action: (Any) -> Unit
}

/**
 * Just a plain holder of a subscription.
 */
open class DefaultSubscription(
    override val eventType: KClass<*>,
    override val owner: Any,
    override val action: (Any) -> Unit
) : Subscription

/**
 * Default implementation of an event bus.
 *
 * Uses a ConcurrentHashMap to be thread safe.
 *
 * Allows to override the executor on which events should be dispatched
 * and the exception handler whenever an action of a subscription fails.
 */
open class DefaultEventBus(
    private val executor: Executor = DirectExecutor(),
    private val handler: EventBusExceptionHandler = LoggingExceptionHandler()
) : EventBus {

    private val subscriptions: ConcurrentMap<KClass<*>, MutableSet<Subscription>> = ConcurrentHashMap()

    override fun <T : Any> subscribe(subscriber: Any, eventType: KClass<T>, action: (T) -> Unit): EventBus {
        val subscriptions = subscriptions.getOrPut(eventType) { mutableSetOf() }
        subscriptions.add(DefaultSubscription(eventType, subscriber, action as (Any) -> Unit))
        return this
    }

    override fun <T : Any> unsubscribe(subscriber: Any, eventType: KClass<T>): EventBus {
        subscriptions[eventType]?.removeAll { it.owner == subscriber }
        return this
    }

    override fun <T : Any> post(event: T) {
        internalPost(event)
    }

    private fun <T : Any> internalPost(event: T) {
        val subscriptions = subscriptions[event::class]
                ?.toList() // copy to prevent ConcurrentModificationException
            ?: return
        for (subscription in subscriptions) {
            try {
                subscription.action.invoke(event)
            } catch (e: Throwable) {
                handler.handle(e, subscription)
            }
        }
    }

    override fun <T : Any> postAsync(event: T) {
        executor.execute { internalPost(event) }
    }
}

/**
 * Exception handler which is called by the event bus whenever a subscription fails to trigger
 * its action.
 */
interface EventBusExceptionHandler {
    fun handle(e: Throwable, subscription: Subscription)
}

/**
 * Default implementation of an EventBusExceptionHandler.
 * Uses JUL to simply log exceptions.
 */
class LoggingExceptionHandler : EventBusExceptionHandler {

    private val log: Logger = Logger.getLogger(LoggingExceptionHandler::class.java.name)

    override fun handle(e: Throwable, subscription: Subscription) {
        if (log.isLoggable(Level.SEVERE)) {
            log.log(Level.SEVERE,
                    "Unexpected error '$e' " +
                            "on subscriber '${subscription.owner}' " +
                            "when dispatching event '${subscription.eventType}'."
            )
        }
    }
}
