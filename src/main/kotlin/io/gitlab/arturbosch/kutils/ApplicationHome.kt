package io.gitlab.arturbosch.kutils

import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap

/**
 * An ApplicationHome represents the home directory of your application.
 * It is located at the `baseDir` on the file system.
 * Convenient functions can resolve/create files and directories within the home directory.
 */
interface ApplicationHome {

    val baseDir: Path

    fun check(path: Path, isDir: Boolean = true, shouldCreate: Boolean = true) = path.apply {
        if (notExists() && shouldCreate) {
            when {
                isDir -> Files.createDirectories(this)
                else -> this.createFile()
            }
        }
    }

    fun resolveFile(additional: String, shouldCreate: Boolean = true): Path =
            check(baseDir.resolve(additional), isDir = false)

    fun resolveDir(additional: String, shouldCreate: Boolean = true): Path =
            check(baseDir.resolve(additional))
}

/**
 * Convenience implementation of an ApplicationHome with properties support.
 */
abstract class ApplicationHomeFolder(
    override val baseDir: Path,
    protected val properties: MutableMap<String, String> = HashMap()
) : ApplicationHome, PropertiesAware {

    override fun property(key: String): String? = properties[key]
    override fun propertyOrDefault(key: String, defaultValue: String): String = property(key) ?: defaultValue

    fun addProperty(key: String, value: String) {
        properties[key] = value
    }

    fun addProperties(properties: Map<String, String>) {
        this.properties.putAll(properties)
    }

    fun addPropertiesFromFile(propertyFile: Path) {
        check(propertyFile.exists())
        propertyFile.open().useLines { lines ->
            for (line in lines) {
                val trimmed = line.trim()
                check(trimmed.contains("=")) { "key=value expected but found $trimmed" }
                val (key, value) = trimmed.substringBefore("=") to trimmed.substringAfter("=")
                properties[key] = value
            }
        }
    }

    fun addPropertiesFromCommaSeparatedString(props: String) {
        props.splitToSequence(',')
                .map(String::trim)
                .map { it.split('=') }
                .onEach { check(it.size == 2) { "key=value expected but found ${it.joinToString()}" } }
                .forEach { properties[it[0]] = it[1] }
    }
}

interface PropertiesAware {
    fun property(key: String): String?
    fun propertyOrDefault(key: String, defaultValue: String): String
}
