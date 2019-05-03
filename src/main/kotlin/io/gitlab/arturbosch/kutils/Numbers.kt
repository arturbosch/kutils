package io.gitlab.arturbosch.kutils

fun Int.positive() = if (this < 0) 0 else this

fun Double.positive() = if (this < 0.0) 0.0 else this
