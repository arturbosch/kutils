package io.gitlab.arturbosch.kutils

import java.util.ServiceLoader

inline fun <reified T> load(classLoader: ClassLoader? = null): ServiceLoader<T> =
    ServiceLoader.load(T::class.java, classLoader ?: T::class.java.classLoader)

