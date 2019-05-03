package io.gitlab.arturbosch.kutils

import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap

/**
 * Transforms the list of pairs to a map, applying a merge strategy for same keys.
 */
fun <K, V> List<Pair<K, List<V>>>.toMergedMap(): Map<K, List<V>> {
    val map = HashMap<K, MutableList<V>>()
    this.forEach {
        map.merge(it.first, it.second.toMutableList()) { l1, l2 -> l1.apply { addAll(l2) } }
    }
    return map
}

/**
 * Merges two maps into one map. Uses a reduce function for clashes. Default reduce is to replace the element.
 */
fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V = { _, b -> b }): Map<K, V> {
    val result = LinkedHashMap<K, V>(this.size + other.size)
    result.putAll(this)
    other.forEach { e -> result[e.key] = result[e.key]?.let { reduce(e.value, it) } ?: e.value }
    return result
}

/**
 * Merges a key value pair into an existing map. Remaps the value if needed.
 */
inline fun <K, V> MutableMap<K, V>.merge(key: K, value: V, mergeFunction: (V, V) -> V) {
    val oldValue = this[key]
    if (oldValue == null) {
        this[key] = value
    } else {
        this[key] = mergeFunction(oldValue, value)
    }
}

/**
 * Adds given element on position 0 without replacing any element.
 */
fun <E> List<E>.plusElementAtBeginning(element: E): List<E> {
    val result = ArrayList<E>(size + 1)
    result.add(element)
    result.addAll(this)
    return result
}

/**
 * Transforms every entry into a string by calling it's toString method.
 */
fun <E> Collection<E>.applyToString(): List<String> = this.map { it.toString() }

/**
 * Replace one element at given index, creating a new immutable list.
 */
fun <E> List<E>.replaceAt(index: Int, element: E): List<E> {
    val list = this.toMutableList()
    list[index] = element
    return list.toList()
}

/**
 * Only do something with this list if it is not empty.
 */
inline fun <E> Collection<E>.ifNotEmpty(function: Collection<E>.() -> Unit) {
    if (this.isNotEmpty()) {
        function.invoke(this)
    }
}

/**
 * Transforms the collection into a LinkedHashMap by using key and value functions.
 */
inline fun <T, K, V> Collection<T>.toMap(keyFunction: (T) -> K, valueFunction: (T) -> V): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    for (element in this) {
        result[keyFunction(element)] = valueFunction(element)
    }
    return result
}

/**
 * Transforms the collection into a HashMap by using key and value functions.
 */
inline fun <T, K, V> Collection<T>.toHashMap(keyFunction: (T) -> K, valueFunction: (T) -> V): HashMap<K, V> {
    val result = HashMap<K, V>()
    for (element in this) {
        result[keyFunction(element)] = valueFunction(element)
    }
    return result
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.singleAsList(): List<T> = if (this.notNull()) listOf(this) else emptyList()

@Suppress("ConvertFlatMapToFlatten")
fun <T> mergeCollections(vararg other: Collection<T>): List<T> = other.flatMap { it }
