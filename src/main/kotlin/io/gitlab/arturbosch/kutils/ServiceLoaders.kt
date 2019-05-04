package io.gitlab.arturbosch.kutils

import java.util.ServiceLoader

inline fun <reified T> load(classLoader: ClassLoader? = null): ServiceLoader<T> =
    ServiceLoader.load(T::class.java, classLoader ?: T::class.java.classLoader)

interface WithPriority {

    val priority: Int get() = 0
}

fun <T : WithPriority> ServiceLoader<T>.firstPrioritized(): T? = sortedBy { it.priority }.lastOrNull()
