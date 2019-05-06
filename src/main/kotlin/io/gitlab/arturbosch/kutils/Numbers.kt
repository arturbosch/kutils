package io.gitlab.arturbosch.kutils

fun Int.positive() = if (this < 0) 0 else this

fun Long.positive() = if (this < 0L) 0L else this

fun Float.positive() = if (this < 0.0f) 0.0f else this

fun Double.positive() = if (this < 0.0) 0.0 else this
