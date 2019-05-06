package io.gitlab.arturbosch.kutils

inline fun <T> T.mapIf(condition: Boolean, function: (T) -> T): T =
    if (condition) function(this) else this

inline fun <T> T.mapIf(condition: (T) -> Boolean, function: (T) -> T): T =
    if (condition(this)) function(this) else this
