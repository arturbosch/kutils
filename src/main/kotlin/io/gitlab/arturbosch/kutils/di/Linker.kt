package io.gitlab.arturbosch.kutils.di

import java.lang.reflect.Constructor
import java.util.HashMap

/**
 * @author Artur Bosch
 */
class Linker {

	val factories: MutableMap<Class<*>, Factory<*>> = HashMap()
	val linkedFactories: MutableMap<Class<*>, Factory<*>> = HashMap()

	fun <T> install(key: Class<T>, factory: Factory<T>) {
		factories.put(key, factory)
	}

	inline fun <reified T> install(factory: Factory<T>) {
		install(T::class.java, factory)
	}

	inline fun <reified T> factoryFor() = factoryFor(T::class.java)

	@Suppress("UNCHECKED_CAST")
	fun <T> factoryFor(key: Class<T>): Factory<T> {
		println("Get factory for $key")
		var factory = linkedFactories[key]
		if (factory == null) {
			println("Link factory for " + key)
			factory = loadFactory(key)
			factory.link(this)
			linkedFactories.put(key, factory)
		}
		return factory as Factory<T>
	}

	private fun <T> loadFactory(key: Class<T>): Factory<*> {
		var factory = factories[key]

		if (factory != null) return factory

		val constructor = findAtInjectConstructor(key)
		if (constructor != null) {
			factory = ReflectiveFactory(constructor)
			if (key.isAnnotationPresent(Singleton::class.java)) {
				factory = SingletonFactory.of(factory)
			}
			return factory
		}

		throw IllegalAccessException("There is no factory registered for $key!")
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> findAtInjectConstructor(type: Class<T>): Constructor<*>? {
		var noArgConstructor: Constructor<*>? = null
		val constructor = type.constructors.find {
			if (it.parameterTypes.isEmpty()) noArgConstructor = it
			it.isAnnotationPresent(Inject::class.java)
		}
		return constructor ?: noArgConstructor
	}

}
