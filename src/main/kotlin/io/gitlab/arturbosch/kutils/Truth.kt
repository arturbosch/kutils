package io.gitlab.arturbosch.kutils

/**
 * Shorthand for null check.
 */
fun Any?.notNull(): Boolean = if (this != null) true else false