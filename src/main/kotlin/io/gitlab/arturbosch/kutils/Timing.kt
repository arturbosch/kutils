package io.gitlab.arturbosch.kutils

/**
 * Measures execution time in millis and returns the result of given bock.
 */
inline fun <T> measureAndReturn(block: () -> T): Pair<Long, T> {
	val start = System.currentTimeMillis()
	val result = block()
	return System.currentTimeMillis() - start to result
}
