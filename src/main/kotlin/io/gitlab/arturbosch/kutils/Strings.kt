package io.gitlab.arturbosch.kutils

/**
 * Replaces a sub sequence with a new sequence starting to search from the end.
 */
fun String.replaceLast(oldChars: String, newChars: String, ignoreCase: Boolean = false): String {
	val index = lastIndexOf(oldChars, ignoreCase = ignoreCase)
	return if (index < 0) this else this.replaceRange(index, index + oldChars.length, newChars)
}