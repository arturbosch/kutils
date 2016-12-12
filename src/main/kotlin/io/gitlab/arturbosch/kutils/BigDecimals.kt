package io.gitlab.arturbosch.kutils

import java.math.BigDecimal

/**
 * Shortcut for creating big decimals of doubles.
 */
fun Double.toBigDecimal(): BigDecimal = BigDecimal.valueOf(this)

/**
 * Allows addition for big decimals using plus.
 */
operator fun BigDecimal.plus(a: BigDecimal): BigDecimal = this.add(a)

/**
 * Allows subtraction for big decimals using minus.
 */
operator fun BigDecimal.minus(a: BigDecimal): BigDecimal = this.subtract(a)

/**
 * Allows multiplication for big decimals using times.
 */
operator fun BigDecimal.times(a: BigDecimal): BigDecimal = this.multiply(a)

/**
 * Allows division for big decimals using div.
 */
operator fun BigDecimal.div(a: BigDecimal): BigDecimal = this.divide(a)