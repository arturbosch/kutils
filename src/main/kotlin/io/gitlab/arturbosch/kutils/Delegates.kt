package io.gitlab.arturbosch.kutils

import java.util.logging.Logger
import kotlin.reflect.KProperty

fun julLogger(): LoggerDelegate = LoggerDelegate()

class LoggerDelegate {

    private var logger: Logger? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        if (logger == null) logger = Logger.getLogger(thisRef.javaClass.name)
        return logger!!
    }
}

fun <T : Any> single(): SingleAssign<T> = SingleAssign()

class SingleAssign<T : Any> {

    private lateinit var _value: T

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        check(this::_value.isInitialized) { "Property ${property.name} has not been assigned yet!" }
        return _value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        check(!this::_value.isInitialized) { "Property ${property.name} has already been assigned!" }
        _value = value
    }
}
