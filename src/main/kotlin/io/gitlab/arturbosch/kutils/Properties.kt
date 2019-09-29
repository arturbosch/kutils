@file:Suppress("detekt.SpreadOperator")

package io.gitlab.arturbosch.kutils

import java.io.InputStream
import java.nio.file.Path

@JvmOverloads
fun loadProperties(
    path: Path,
    separator: String = "=",
    commentStarters: Array<String> = arrayOf("#")
): Map<String, String> = path.useNormalizedLines(*commentStarters) { it.toPropertiesMap(separator) }

@JvmOverloads
fun loadProperties(
    input: InputStream,
    separator: String = "=",
    commentStarters: Array<String> = arrayOf("#")
): Map<String, String> = input.useNormalizedLines(*commentStarters) { it.toPropertiesMap(separator) }
