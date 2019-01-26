package io.gitlab.arturbosch.kutils

/**
 * Not so ugly way to get a qualified class name.
 */
inline fun <reified T> className(): String = T::class.java.name

/**
 * Not so ugly way to get a simple class name.
 */
inline fun <reified T> simpleClassName(): String = T::class.java.simpleName
