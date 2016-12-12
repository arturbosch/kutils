package io.gitlab.arturbosch.kutils

import java.nio.file.Files
import java.nio.file.Path

/**
 * Does this path represent a file?
 */
fun Path.isFile(): Boolean = Files.isRegularFile(this)

/**
 * Does this path represent a directory?
 */
fun Path.isDirectory(): Boolean = Files.isDirectory(this)

/**
 * Tests if this path has the given ending.
 */
fun Path.hasEnding(ending: String): Boolean = this.toAbsolutePath().toString().endsWith(ending)