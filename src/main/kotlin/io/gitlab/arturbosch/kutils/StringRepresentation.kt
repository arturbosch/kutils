package io.gitlab.arturbosch.kutils

/**
 * Forces classes to explicitly override the toString()-function.
 */
interface StringRepresentation {
    override fun toString(): String
}
