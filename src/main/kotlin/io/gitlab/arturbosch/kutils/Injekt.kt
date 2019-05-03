@file:Suppress("UNCHECKED_CAST")

package io.gitlab.arturbosch.kutils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Defines minimal logic for dependency injection.
 */
interface Injektor {

    @Throws(InvalidDependency::class, CircularDependency::class)
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
        return runCatching { factory.produce() as T }
            .getOrElse { throw (if (it is StackOverflowError) CircularDependency(type) else it) }
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

/* Kotlin convenience functions. */

inline fun <reified T : Any> Injektor.get(): T = get(typeRef<T>().type)

inline fun <reified T : Any> Injektor.lazy(): Lazy<T> = kotlin.lazy { get<T>() }

inline fun <reified T : Any> Injektor.lazy(crossinline init: (T) -> Unit): Lazy<T> =
    kotlin.lazy { get<T>().also(init) }

inline fun <reified T : Any> Injektor.addSingleton(instance: T) {
    addSingletonFactory(typeRef()) { instance }
}

inline fun <reified T : Any> Injektor.withSingleton(instance: T): T {
    addSingleton(instance)
    return instance
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

// Errors

/**
 * Is thrown when the [Injektor] does not have a type registered.
 */
open class InvalidDependency(type: Type) : IllegalStateException("No '$type' registered.")

/**
 * Is thrown when the [Injektor] can't resolve a dependency due to circular usages.
 */
@Suppress("NO_REFLECTION_IN_CLASS_PATH")
open class CircularDependency(clazz: Type)
    : RuntimeException("Circular dependencies detected when resolving ${clazz.typeName}")
