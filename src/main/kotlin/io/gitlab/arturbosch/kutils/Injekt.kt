@file:Suppress("UNCHECKED_CAST")

package io.gitlab.arturbosch.kutils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Defines minimal logic for dependency injection.
 */
interface Injektor {

	@Throws(InvalidDependency::class)
	fun <T : Any> get(type: Type): T

	fun <T : Any> addFactory(typeReference: TypeReference<T>, factory: () -> T)
	fun <T : Any> addSingletonFactory(typeReference: TypeReference<T>, factory: () -> T)
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

	override fun <T : Any> addFactory(typeReference: TypeReference<T>, factory: () -> T) {
		factories[typeReference.type] = Factory.ProducingFactory(factory)
	}

	override fun <T : Any> addSingletonFactory(typeReference: TypeReference<T>, factory: () -> T) {
		factories[typeReference.type] = Factory.SingletonFactory(factory)
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

inline fun <reified T : Any> Injektor.get(): T = get(typeRef<T>().type)

inline fun <reified T : Any> Injektor.lazy(): Lazy<T> = lazy { get<T>() }

inline fun <reified T : Any> Injektor.addSingleton(instance: T) {
	addSingletonFactory(typeRef()) { instance }
}

inline fun <reified T : Any> Injektor.addSingletonFactory(noinline instance: () -> T) {
	addSingletonFactory(typeRef(), instance)
}

inline fun <reified T : Any> Injektor.addFactory(noinline instance: () -> T) {
	addFactory(typeRef(), instance)
}

// type reference highly inspired by https://github.com/kohesive/injekt/

inline fun <reified T : Any> typeRef(): FullTypeReference<T> = object : FullTypeReference<T>() {}

@Suppress("unused")
interface TypeReference<T> {
	val type: Type
}

/**
 * Wraps the real type. Adapted from Injekt.
 */
abstract class FullTypeReference<T> protected constructor() : TypeReference<T> {
	override val type: Type = javaClass.genericSuperclass.let { superClass ->
		if (superClass is Class<*>) {
			throw IllegalArgumentException("TypeReference constructed without actual type information")
		}
		val typeArguments = (superClass as ParameterizedType).actualTypeArguments
		check(typeArguments.size == 1) { "A type reference must exactly contain one type argument." }
		typeArguments[0]
	}
}
