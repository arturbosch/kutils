package io.gitlab.arturbosch.kutils

import java.util.logging.Logger
import kotlin.reflect.KProperty

/**
 * @author Artur Bosch
 */
fun julLogger() = LoggerDelegate()

class LoggerDelegate {

	private var logger: Logger? = null

	operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
		if (logger == null) logger = Logger.getLogger(thisRef.javaClass.name)
		return logger!!
	}

}
