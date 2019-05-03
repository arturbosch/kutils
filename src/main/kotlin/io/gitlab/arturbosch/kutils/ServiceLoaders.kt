package io.gitlab.arturbosch.kutils

import java.util.ServiceLoader

inline fun <reified T> load(): ServiceLoader<T> = ServiceLoader.load(T::class.java)
