package io.gitlab.arturbosch.kutils

inline fun <T> Sequence<T>.mapIf(
    condition: Boolean,
    crossinline function: (T) -> T
): Sequence<T> = if (condition) this.map { function(it) } else this


inline fun <T> Sequence<T>.mapIf(
    crossinline condition: (Sequence<T>) -> Boolean,
    crossinline function: (T) -> T
): Sequence<T> = if (condition(this)) this.map { function(it) } else this

/**
 * Consumes the whole sequence and does not return anything.
 * Can be helpful when iterating over the sequence is important for side effects but not for the result.
 */
internal fun <T> Sequence<T>.consume() {
    this.toList()
}
