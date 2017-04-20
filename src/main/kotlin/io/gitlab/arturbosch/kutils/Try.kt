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

	infix inline fun <Result> then(block: (T) -> Result): Try<Result> {
		return if (value != null) {
			try {
				Try(block.invoke(value), null)
			} catch (any: Throwable) {
				Try(null, any)
			}
		} else {
			Try(null, error)
		}
	}

	infix inline fun <Result> zip(block: (T) -> Try<Result>): Try<Result> {
		return if (value != null) {
			try {
				block.invoke(value)
			} catch (any: Throwable) {
				Try(null, any)
			}
		} else {
			Try(null, error)
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

	infix inline fun <New> compose(block: (T?, Throwable?) -> New): Try<New> {
		try {
			return Try(block.invoke(value, error), null)
		} catch (any: Throwable) {
			return Try(null, any)
		}
	}

	companion object {
		operator inline fun <T> invoke(block: () -> T): Try<T> = Companion.to(block)

		inline fun <T> to(block: () -> T): Try<T> {
			try {
				return Try(block.invoke(), null)
			} catch (any: Throwable) {
				return Try(null, any)
			}
		}
	}
}

inline fun <T> tryTo(block: () -> T): Try<T> = Try.to(block)