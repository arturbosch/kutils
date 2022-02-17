package io.gitlab.arturbosch.kutils

fun Int.positive(): Int = if (this < 0) 0 else this

fun Long.positive(): Long = if (this < 0L) 0L else this

fun Float.positive(): Float = if (this < 0.0f) 0.0f else this

fun Double.positive(): Double = if (this < 0.0) 0.0 else this
