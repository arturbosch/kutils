package io.gitlab.arturbosch.kutils

/**
 * Add leading zero if number has only one digit.
 */
fun Number.toTimeString() = toString().apply {
	if (length == 1) {
		return "0$this"
	}
}