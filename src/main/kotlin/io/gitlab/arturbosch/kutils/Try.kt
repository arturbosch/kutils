package io.gitlab.arturbosch.kutils

/**
 * @author Artur Bosch
 */
data class Try<out T>(val value: T?, val error: Throwable?) {

	init {
		require(value != null || error != null) {
			"Value of Try must not be evaluated to null!"
		}
	}

	infix inline fun onSuccess(block: (T) -> Unit): Try<T> {
		if (value != null) {
			block.invoke(value)
		}
		return this
	}

	infix inline fun onError(block: (Throwable) -> Unit): Try<T> {
		if (error != null) {
			block.invoke(error)
		}
		return this
	}

	companion object {
		inline fun <T> to(block: () -> T): Try<T> {
			try {
				return Try(block.invoke(), null)
			} catch (any: Throwable) {
				return Try(null, any)
			}
		}
	}
}

fun <T> tryTo(block: () -> T): Try<T> = Try.to(block)