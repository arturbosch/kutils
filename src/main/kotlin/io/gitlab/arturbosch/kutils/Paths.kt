@file:Suppress("NOTHING_TO_INLINE", "TooManyFunctions")

package io.gitlab.arturbosch.kutils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.stream.Stream
import kotlin.streams.asSequence

/**
 * Converts this string to a path object.
 */
inline fun String.asPath(): Path = Paths.get(this)

/**
 * Converts this string to a path object.
 */
inline fun String.asHomeAwarePath(): Path = Paths.get(
    if (startsWith("~" + File.separator)) {
        replaceFirst("~", System.getProperty("user.home"))
    } else {
        this
    }
)

/**
 * Returns a normalized absolute path specified by given string.
 */
inline fun path(path: String): Path = path.asHomeAwarePath().toAbsolutePath().normalize()

/**
 * Does this path represent a file?
 */
inline fun Path.isFile(): Boolean = Files.isRegularFile(this)

/**
 * Does this path represent a directory?
 */
inline fun Path.isDirectory(): Boolean = Files.isDirectory(this)

/**
 * Tests if this path has the given ending.
 */
inline fun Path.hasEnding(ending: String): Boolean = this.toAbsolutePath().toString().endsWith(ending)

/**
 * Tests if this path exists.
 */
inline fun Path.exists() = Files.exists(this)

/**
 * Tests if this path does not exist.
 */
inline fun Path.notExists() = Files.notExists(this)

/**
 * Reads the file content into a string.
 */
inline fun Path.readText(charSet: Charset = Charsets.UTF_8) = String(Files.readAllBytes(this), charSet)

/**
 * Reads the file content into a byte array.
 */
inline fun Path.readBytes() = Files.readAllBytes(this)

/**
 * Reads all lines of the file represented by this path.
 */
inline fun Path.readLines(): MutableList<String> = Files.readAllLines(this)

/**
 * Streams all lines of the file represented by this path.
 */
inline fun Path.streamLines(): Stream<String> = Files.lines(this)

/**
 * Reads all lines of the file represented by this path.
 */
inline fun <T> Path.useLines(
    charSet: Charset = Charsets.UTF_8,
    block: (Sequence<String>) -> T
): T = toFile().useLines(charset = charSet, block = block)

/**
 * Copies content of this path to target path.
 */
inline fun Path.copy(target: Path): Path = Files.copy(this, target)

/**
 * Shortcut to write the content of a string to a file.
 */
inline fun Path.write(
    content: String,
    charSet: Charset = Charsets.UTF_8
): Path = Files.write(this, content.toByteArray(charSet))

/**
 * Shortcut to write the bytes to a file.
 */
inline fun Path.write(content: ByteArray): Path = Files.write(this, content)

/**
 * Opens a buffered reader from this path.
 */
inline fun Path.open(): BufferedReader = Files.newBufferedReader(this)

/**
 * Creates system file based on this path. Also creates all parent directories.
 */
inline fun Path.createFile(): Path = this.apply {
    parent.createDir()
    if (this.notExists()) {
        Files.createFile(this)
    }
}

/**
 * Creates system folder based on this path.
 */
inline fun Path.createDir(): Path = Files.createDirectories(this)

/**
 * Streams over the given path..
 * Optionally you can exclude the base path (= this path) from the stream.
 * Cannot be reused due to Stream nature.
 */
inline fun Path.stream(
    excludeRoot: Boolean = false,
    maxDepth: Int = Int.MAX_VALUE
): Stream<Path> =
    when (excludeRoot) {
        true -> Files.walk(this, maxDepth).filter { it != this }
        else -> Files.walk(this, maxDepth)
    }

/**
 * Same as [stream] but returns a [Sequence].
 * The function name warns you that this sequence can only be consumed once.
 */
inline fun Path.streamOnce(
    excludeRoot: Boolean = false,
    maxDepth: Int = Int.MAX_VALUE
): Sequence<Path> = stream(excludeRoot, maxDepth).asSequence()

/**
 * Walks through the given path. Sequence can be reused.
 */
fun Path.walk(
    excludeRoot: Boolean = false,
    direction: FileWalkDirection = FileWalkDirection.TOP_DOWN
): Sequence<Path> {
    val root = toFile()
    return when (excludeRoot) {
        true -> root.walk(direction).filter { it != root }.map { it.toPath() }
        else -> root.walk(direction).map { it.toPath() }
    }
}

/**
 * Tests if this path exists, if not make it nullable.
 */
inline fun Path.ifExists(): Path? = if (Files.exists(this)) this else null

/**
 * Tests if this path does not exist to make it nullable
 */
inline fun Path.ifNotExists(): Path? = if (Files.notExists(this)) this else null

/**
 * Appends given [content] to this file.
 */
inline fun Path.append(content: String): Path =
    Files.write(this, content.toByteArray(), StandardOpenOption.APPEND)

/**
 * Returns just the fileName without extension. E.g. 'foo.bar.txt' will return 'foo'.
 */
inline fun Path.nameWithoutExtension(): String = this.name().substringBefore(".")

/**
 * Returns just the fileName as string.
 */
inline fun Path.name(): String = this.fileName.toString()

/**
 * Returns the extension of a path. E.g. 'foo.bar.txt' will return 'bar.txt'.
 */
inline fun Path.extension(): String = this.fileName.toString().substringAfter(".")

/**
 * Lists paths in current folder or returns an empty sequence if not a directory.
 */
inline fun Path.list(): Sequence<Path> = toFile().list()?.asSequence()?.map { it.asPath() } ?: emptySequence()

/**
 * Opens this path as an input stream. Needs to be closed.
 */
inline fun Path.inputStream(): InputStream = Files.newInputStream(this)

/**
 * Opens this path as an output stream. Needs to be closed.
 */
inline fun Path.outputStream(): OutputStream = Files.newOutputStream(this)

/**
 * Opens this path as an output stream. Needs to be closed.
 */
inline fun Path.writer(
    charSet: Charset = Charsets.UTF_8
): BufferedWriter = Files.newBufferedWriter(this, charSet)
