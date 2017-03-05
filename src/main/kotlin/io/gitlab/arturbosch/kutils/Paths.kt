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

/**
 * Tests if this path exists.
 */
fun Path.exists() = Files.exists(this)

/**
 * Tests if this path does not exist.
 */
fun Path.notExists() = Files.notExists(this)

/**
 * Reads the file content into a string.
 */
fun Path.readText() = String(Files.readAllBytes(this))

/**
 * Reads all lines of the file represented by this path.
 */
fun Path.readLines() = Files.readAllLines(this)

/**
 * Copies content of this path to target path.
 */
fun Path.copy(target: Path) = Files.copy(this, target)