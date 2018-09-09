@file:Suppress("UNCHECKED_CAST")

package io.gitlab.arturbosch.kutils

import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Defines minimal logic for dependency injection.
 */
interface Injektor {

	@Throws(InvalidDependency::class)
	fun <T : Any> get(type: Type): T

	fun <T : Any> addFactory(type: Type, factory: () -> T)
	fun <T : Any> addSingletonFactory(type: Type, factory: () -> T)
}

/**
 * Default implementation of the [Injektor] interface.
 * Uses a concurrent hash map to store factories which are used to create instances.
 */
open class DefaultInjektor : Injektor {

	protected val factories = ConcurrentHashMap<Type, Factory<*>>()

	override fun <T : Any> get(type: Type): T {
		val factory = factories[type] ?: throw InvalidDependency(type)
		return factory.produce() as T
	}

	override fun <T : Any> addFactory(type: Type, factory: () -> T) {
		factories[type] = Factory.ProducingFactory(factory)
	}

	override fun <T : Any> addSingletonFactory(type: Type, factory: () -> T) {
		factories[type] = Factory.SingletonFactory(factory)
	}
}

/**
 * Decides how to construct objects.
 */
sealed class Factory<T : Any>(private val producer: () -> T) {

	abstract fun produce(): T

	class SingletonFactory<T : Any>(producer: () -> T) : Factory<T>(producer) {
		private val instance: T by lazy(producer)
		override fun produce(): T = instance

	}

	class ProducingFactory<T : Any>(private val producer: () -> T) : Factory<T>(producer) {
		override fun produce(): T = producer.invoke()
	}
}

/**
 * Is thrown when the [Injektor] does not have a type registered.
 */
class InvalidDependency(type: Type) : IllegalStateException("No '$type' registered.")

/* Kotlin convenience functions. */

inline fun <reified T : Any> Injektor.get(): T = get(T::class.java)

inline fun <reified T : Any> Injektor.addSingleton(instance: T) {
	addSingletonFactory(T::class.java) { instance }
}

inline fun <reified T : Any> Injektor.addSingletonFactory(noinline instance: () -> T) {
	addSingletonFactory(T::class.java, instance)
}

inline fun <reified T : Any> Injektor.addFactory(noinline instance: () -> T) {
	addFactory(T::class.java, instance)
}
