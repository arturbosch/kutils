package io.gitlab.arturbosch.kutils

/**
 * Shorthand for null check.
 */
fun Any?.notNull(): Boolean = this != null

/**
 * Shorthand for null check for booleans.
 */
fun Boolean?.notNullOrFalse(): Boolean = this != null && this
