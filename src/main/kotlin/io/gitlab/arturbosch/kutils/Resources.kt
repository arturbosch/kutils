package io.gitlab.arturbosch.kutils

import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
internal object Resources

fun resource(name: String): URI {
	val explicitName = if (name.startsWith("/")) name else "/$name"
	val resource = Resources::class.java.getResource(explicitName)
	requireNotNull(resource) { "Make sure the resource '$name' exists!" }
	return resource.toURI()
}

fun resourceAsStream(name: String): InputStream = Files.newInputStream(Paths.get(resource(name)))

fun resourceAsString(name: String): String = String(Files.readAllBytes(Paths.get(resource(name))))
