package io.gitlab.arturbosch.kutils

/**
 * Runs a function on each element of the array.
 * [kotlin.collections.onEach] equivalent on arrays.
 */
inline fun <T> Array<T>.onEach(function: (T) -> Unit): Array<T> {
    for (elem in this) {
        function.invoke(elem)
    }
    return this
}
