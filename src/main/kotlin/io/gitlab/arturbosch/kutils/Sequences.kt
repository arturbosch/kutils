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

fun Sequence<String>.applyFilters(prefixesToFilter: Array<out String>): Sequence<String> =
    transformIf(prefixesToFilter.isNotEmpty()) {
        it.filterNot { line -> prefixesToFilter.any { prefix -> line.startsWith(prefix) } }
    }

fun Sequence<String>.toPropertiesMap(separator: String = "="): Map<String, String> =
    map { it.split(separator) }
        .filter { it.size == 2 }
        .associate { it[0] to it[1] }
