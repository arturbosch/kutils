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

	inline infix fun <Result> then(block: (T) -> Result) = if (value != null) {
		try {
			Try(block.invoke(value), null)
		} catch (any: Throwable) {
			Try(null, any)
		}
	} else {
		Try(null, error)
	}

	inline infix fun <Result> zip(block: (T) -> Try<Result>) = if (value != null) {
		try {
			block.invoke(value)
		} catch (any: Throwable) {
			Try(null, any)
		}
	} else {
		Try(null, error)
	}

	inline infix fun onSuccess(block: (T) -> Unit): Try<T> {
		if (value != null) {
			block.invoke(value)
		}
		return this
	}

	inline infix fun onError(block: (Throwable) -> Unit): Try<T> {
		if (error != null) {
			block.invoke(error)
		}
		return this
	}

	inline infix fun <New> compose(block: (T?, Throwable?) -> New): Try<New> = try {
		Try(block.invoke(value, error), null)
	} catch (any: Throwable) {
		Try(null, any)
	}

	companion object {
		inline operator fun <T> invoke(block: () -> T): Try<T> = to(block)

		inline fun <T> to(block: () -> T): Try<T> = try {
			Try(block.invoke(), null)
		} catch (any: Throwable) {
			Try(null, any)
		}
	}
}

inline fun <T> tryTo(block: () -> T): Try<T> = Try.to(block)
