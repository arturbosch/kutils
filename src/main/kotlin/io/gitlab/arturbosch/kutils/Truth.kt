package io.gitlab.arturbosch.kutils

/**
 * Shorthand for null check.
 */
fun Any?.notNull(): Boolean = this != null

/**
 * Shorthand null check for nullable booleans.
 */
fun Boolean?.isTrue(): Boolean = this != null && this
