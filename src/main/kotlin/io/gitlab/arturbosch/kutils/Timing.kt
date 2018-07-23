package io.gitlab.arturbosch.kutils

/**
 * @author Artur Bosch
 */
inline fun <T> measureAndReturn(block: () -> T): Pair<Long, T> {
	val start = System.currentTimeMillis()
	val result = block()
	return System.currentTimeMillis() - start to result
}
