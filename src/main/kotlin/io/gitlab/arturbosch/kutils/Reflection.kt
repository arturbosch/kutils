package io.gitlab.arturbosch.kutils

/**
 * Not so ugly way to get a qualified class name.
 */
inline fun <reified T> className(): String = T::class.java.name
