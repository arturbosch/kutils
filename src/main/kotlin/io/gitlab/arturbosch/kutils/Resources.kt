package io.gitlab.arturbosch.kutils

import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Identifies a resource with given name and returns it URI representation.
 * May throw IllegalArgumentException if the resource does not exist.
 */
fun resource(name: String): URI {
    val explicitName = if (name.startsWith("/")) name else "/$name"
    val resource = Unit::class.java.getResource(explicitName)
    requireNotNull(resource) { "Make sure the resource '$name' exists!" }
    return resource.toURI()
}

/**
 * Streams a resource. May throw IllegalArgumentException on resource absence.
 */
fun resourceAsStream(name: String): InputStream = Files.newInputStream(Paths.get(resource(name)))

/**
 * Loads a resource into a string. May throw IllegalArgumentException on resource absence.
 */
fun resourceAsString(name: String): String = String(Files.readAllBytes(Paths.get(resource(name))))

/**
 * Loads a resource and returns it's path representation if possible.
 */
fun resourceAsPath(name: String): Path = Paths.get(resource(name))
