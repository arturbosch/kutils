package io.gitlab.arturbosch.kutils

interface WithPriority {

    @JvmDefault
    val priority: Int
        get() = 0
}
