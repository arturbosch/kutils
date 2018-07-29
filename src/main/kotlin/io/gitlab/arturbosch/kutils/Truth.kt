package io.gitlab.arturbosch.kutils

/**
 * Shorthand for null check.
 */
fun Any?.notNull(): Boolean = this != null

/**
 * Shorthand for null check for booleans.
 */
fun Boolean?.notNullOrFalse(): Boolean = this != null && this

@Suppress("NOTHING_TO_INLINE")
inline infix fun Boolean.xor(other: Boolean): Boolean = this && !other || !this && other
