package io.gitlab.arturbosch.kutils

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Consumes the stream and collects entries to a list.
 */
fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList<T>())
