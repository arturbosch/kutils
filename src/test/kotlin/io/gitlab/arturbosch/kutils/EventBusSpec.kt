package io.gitlab.arturbosch.kutils

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertFailsWith

/**
 * @author Artur Bosch
 */
internal class EventBusSpec : StringSpec({

	"allows to subscribe to events" {

		val bus = DefaultEventBus()
		val actual = AtomicInteger()

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				actual.incrementAndGet()
				bus.unsubscribe<BroadcastEvent>(this)
			}
		}

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				actual.incrementAndGet()
				bus.unsubscribe<BroadcastEvent>(this)
			}
		}

		actual.get() shouldBe 0
		bus.post(BroadcastEvent)
		actual.get() shouldBe 2
		bus.post(BroadcastEvent)
		actual.get() shouldBe 2
	}

	"allows to override dispatcher" {

		val bus = DefaultEventBus(Executors.newSingleThreadExecutor())
		val actual = AtomicInteger()
		val countDownLatch = CountDownLatch(2)

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				actual.incrementAndGet()
				countDownLatch.countDown()
			}
		}

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				actual.incrementAndGet()
				countDownLatch.countDown()
			}
		}

		actual.get() shouldBe 0
		bus.post(BroadcastEvent)
		countDownLatch.await(1L, TimeUnit.SECONDS)
		actual.get() shouldBe 2
	}

	"logs errors on default" {
		val bus = DefaultEventBus()

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				throw IllegalStateException("oO")
			}
		}
		bus.post(BroadcastEvent)
	}

	"allows to override exception handler" {
		val bus = DefaultEventBus(
				handler = object : EventBusExceptionHandler {
					override fun handle(e: Throwable, subscription: Subscription) {
						throw UnsupportedOperationException("not implemented")
					}
				}
		)

		object {
			init {
				bus.subscribe<BroadcastEvent>(this) { run() }
			}

			fun run() {
				throw IllegalStateException("oO")
			}
		}

		assertFailsWith<UnsupportedOperationException> {
			bus.post(BroadcastEvent)
		}
	}
})

object BroadcastEvent
