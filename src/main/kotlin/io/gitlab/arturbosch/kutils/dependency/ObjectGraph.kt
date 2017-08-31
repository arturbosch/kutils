package io.gitlab.arturbosch.kutils.dependency

/**
 * @author Artur Bosch
 */
class ObjectGraph(private val linker: Linker) {
	fun <T> get(key: Class<T>): T = linker.factoryFor(key).get()
	inline fun <reified T> get(): T = get(T::class.java)
}
