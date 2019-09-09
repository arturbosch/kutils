package io.gitlab.arturbosch.kutils

inline fun <T> T.transformIf(condition: Boolean, function: (T) -> T): T =
    if (condition) function(this) else this

inline fun <T> T.transformIf(condition: (T) -> Boolean, function: (T) -> T): T =
    if (condition(this)) function(this) else this
