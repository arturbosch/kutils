package io.gitlab.arturbosch.kutils

import kotlin.reflect.KProperty

/*
 * Adapted from TornadoFx by Edvin Syse - https://github.com/edvin/tornadofx
 */

enum class SingleAssignThreadSafetyMode {
	SYNCHRONIZED,
	NONE
}

fun <T> singleAssign(threadSafetyMode: SingleAssignThreadSafetyMode
					 = SingleAssignThreadSafetyMode.SYNCHRONIZED): SingleAssign<T> =
		if (threadSafetyMode == SingleAssignThreadSafetyMode.SYNCHRONIZED) SynchronizedSingleAssign()
		else UnsynchronizedSingleAssign()

private object UNINITIALIZED_VALUE

interface SingleAssign<T> {
	fun isInitialized(): Boolean
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

private class SynchronizedSingleAssign<T> : SingleAssign<T> {

	@Volatile
	private var initialized = false

	@Volatile
	private var _value: Any? = UNINITIALIZED_VALUE

	override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		if (!initialized) {
			throw IllegalStateException("Value has not been assigned yet!")
		}
		@Suppress("UNCHECKED_CAST")
		return _value as T
	}

	override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		synchronized(this) {
			if (initialized) {
				throw IllegalStateException("Value has already been assigned!")
			}
			_value = value
			initialized = true
		}
	}

	override fun isInitialized() = initialized
}

private class UnsynchronizedSingleAssign<T> : SingleAssign<T> {

	private var initialized = false
	private var _value: Any? = UNINITIALIZED_VALUE

	override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		if (!initialized) {
			throw Exception("Value has not been assigned yet!")
		}
		@Suppress("UNCHECKED_CAST")
		return _value as T
	}

	override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		if (initialized) {
			throw Exception("Value has already been assigned!")
		}
		_value = value
		initialized = true
	}

	override fun isInitialized() = initialized
}
