@file:Suppress("NOTHING_TO_INLINE")

package io.gitlab.arturbosch.kutils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Shorthand for null check.
 */
@UseExperimental(ExperimentalContracts::class)
inline fun Any?.notNull(): Boolean {
    contract {
        returns(true) implies (this@notNull != null)
    }
    return this != null
}

/**
 * Shorthand null check for nullable booleans.
 */
@UseExperimental(ExperimentalContracts::class)
inline fun Boolean?.isTrue(): Boolean {
    contract {
        returns(true) implies (this@isTrue != null)
    }
    return this != null && this
}
