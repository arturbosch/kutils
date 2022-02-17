package io.gitlab.arturbosch.kutils

import java.io.Reader
import java.io.StringReader
import java.nio.file.Path
import java.util.Properties

/**
 * An ApplicationHome represents the home directory of your application.
 * It is located at the `baseDir` on the file system.
 * Convenient functions can resolve/create files and directories within the home directory.
 */
interface ApplicationHome {

    val baseDir: Path

    @JvmDefault
    fun check(path: Path, isDir: Boolean = true, shouldCreate: Boolean = true): Path = path.apply {
        if (notExists() && shouldCreate) {
            when {
                isDir -> this.createDir()
                else -> this.createFile()
            }
        }
    }

    @JvmDefault
    fun resolveFile(additional: String, shouldCreate: Boolean = true): Path =
        check(baseDir.resolve(additional), isDir = false, shouldCreate = shouldCreate)

    @JvmDefault
    fun resolveDir(additional: String, shouldCreate: Boolean = true): Path =
        check(baseDir.resolve(additional), isDir = true, shouldCreate = shouldCreate)
}

/**
 * Convenience implementation of an ApplicationHome with properties support.
 */
abstract class ApplicationHomeFolder(
    final override val baseDir: Path,
    protected val properties: MutableMap<String, String> = HashMap(),
    onFirstStart: () -> Unit = {}
) : ApplicationHome, PropertiesAware {

    init {
        if (baseDir.notExists()) {
            baseDir.createDir()
            onFirstStart()
        }
    }

    override fun property(key: String): String? = properties[key]
    override fun propertyOrDefault(key: String, defaultValue: String): String = property(key) ?: defaultValue

    fun addProperty(key: String, value: String) {
        properties[key] = value
    }

    fun addProperties(properties: Map<String, String>) {
        this.properties.putAll(properties)
    }

    fun addPropertiesFromFile(propertyFile: Path) {
        check(propertyFile.exists()) { "File '$propertyFile' for property loading does not exist." }
        propertyFile.open().use { addPropertiesFromReader(it) }
    }

    fun addPropertiesFromSeparatedString(props: String, separator: Char = ',') {
        val content = props.replace(separator, '\n')
        StringReader(content).use { addPropertiesFromReader(it) }
    }

    fun addPropertiesFromReader(reader: Reader) {
        Properties().apply {
            load(reader)
            forEach { properties[it.key.toString()] = it.value.toString() }
        }
    }

    internal fun internalProperties() = properties
}

interface PropertiesAware {
    fun property(key: String): String?
    fun propertyOrDefault(key: String, defaultValue: String): String
}

abstract class ReloadableConfig(
    private val wrapped: ApplicationHomeFolder
) : ApplicationHome by wrapped, PropertiesAware by wrapped {

    val readOnlyProperties: Map<String, String>
        get() = wrapped.internalProperties().toMap()

    protected fun clearProperties() {
        wrapped.internalProperties().clear()
    }

    abstract fun reload()
}
