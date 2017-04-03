package io.gitlab.arturbosch.kutils

/**
 * Replaces a sub sequence with a new sequence starting to search from the end.
 */
fun String.replaceLast(oldChars: String, newChars: String, ignoreCase: Boolean = false): String {
	val index = lastIndexOf(oldChars, ignoreCase = ignoreCase)
	return if (index < 0) this else this.replaceRange(index, index + oldChars.length, newChars)
}

/**
 * Counts how often a specific part is inside the source string.
 */
fun frequency(source: String, part: String): Int {

	if (source.isEmpty() || part.isEmpty()) {
		return 0
	}

	var count = 0
	var pos = source.indexOf(part, 0)
	while (pos != -1) {
		pos += part.length
		count++
		pos = source.indexOf(part, pos)
	}

	return count
}