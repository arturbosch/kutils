package io.gitlab.arturbosch.kutils

import java.util.Arrays
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Collect entries of stream into a list.
 */
fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList<T>())

/**
 * Collect entries of stream into a set.
 */
fun <T> Stream<T>.toSet(): Set<T> = collect(Collectors.toSet<T>())

/**
 * Collect entries of stream into a collection providing by the factory.
 */
fun <T, C : MutableCollection<T>> Stream<T>.into(factory: () -> C): C =
    collect(Collectors.toCollection { factory.invoke() })

/**
 * Converts this array to a java stream.
 */
fun <T> Array<T>.stream(): Stream<T> = Arrays.stream(this)
