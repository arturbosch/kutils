package io.gitlab.arturbosch.kutils.di

import java.lang.reflect.Constructor
import java.util.ArrayList


/**
 * @author Artur Bosch
 */

abstract class Factory<out T> {
	abstract fun get(): T
	open fun link(linker: Linker) {}
}

class ValueFactory<out T> private constructor(val value: T) : Factory<T>() {
	override fun get() = value

	companion object {
		fun <T> of(value: T) = ValueFactory(value)
	}
}

class SingletonFactory<out T> private constructor(val factory: Factory<T>) : Factory<T>() {

	private var value: T? = null

	@Suppress("UNCHECKED_CAST")
	override fun get(): T {
		if (value == null) {
			synchronized(this) {
				if (value == null) {
					value = factory.get()
				}
			}
		}
		return value!!
	}

	companion object {
		fun <T> of(value: Factory<T>) = SingletonFactory(value)
	}
}

class ReflectiveFactory<T>(val constructor: Constructor<T>) : Factory<T>() {

	private val factories = ArrayList<Factory<*>>()

	override fun link(linker: Linker) {
		constructor.parameterTypes.mapTo(factories) { linker.factoryFor(it) }
	}

	override fun get(): T {
		val dependencies = arrayOfNulls<Any>(factories.size)
		for ((index, factory) in factories.withIndex()) {
			dependencies[index] = factory.get()
		}
		return try {
			constructor.newInstance(*dependencies)
		} catch (e: ReflectiveOperationException) {
			throw DependencyInjectionFailure(e)
		} catch (e: IllegalArgumentException) {
			throw DependencyInjectionFailure(e)
		}
	}
}
