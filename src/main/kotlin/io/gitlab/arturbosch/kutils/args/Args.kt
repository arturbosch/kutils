package io.gitlab.arturbosch.kutils.args

import kotlin.properties.ReadOnlyProperty

open class Args(parser: ArgParser) {

    val help: Boolean by parser.get("help", false)
}

inline fun <reified T : Any> ArgParser.getOrNull(longForm: String, default: T? = null): ReadOnlyProperty<Args, T?> {
    val value: Any? = getValue(longForm, default)
    return ReadOnlyProperty { _, _ -> value as T? }
}

inline fun <reified T : Any> ArgParser.getValue(longForm: String, default: T?): Any? = when (T::class) {
    Int::class -> intValue(longForm, default as Int?)
    Boolean::class -> boolValue(longForm, default as Boolean?)
    Double::class -> doubleValue(longForm, default as Double?)
    Long::class -> longValue(longForm, default as Long?)
    String::class -> stringValue(longForm, default as String?)
    else -> error("Unsupported argument type '${T::class}'.")
}

inline fun <reified T : Any> ArgParser.get(longForm: String, default: T? = null): ReadOnlyProperty<Args, T> {
    val value: Any? = getValue(longForm, default)
    check(value != null) { "No option registered for '$longForm'" }
    return ReadOnlyProperty { _, _ -> value as T }
}
