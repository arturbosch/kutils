package io.gitlab.arturbosch.kutils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

fun LocalDate.toDate(): Date = Date.from(toInstant())

fun LocalDate.toInstant(): Instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()

fun LocalDateTime.toDate(): Date = Date.from(toInstant())

fun LocalDateTime.toInstant(): Instant = this.atZone(ZoneId.systemDefault()).toInstant()

fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

fun Date.toLocalDate(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun Date.toLocalDateTime(): LocalDateTime = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Date.toZonedDateTime(): ZonedDateTime = this.toInstant().atZone(ZoneId.systemDefault())

fun Instant.toLocalDate(): LocalDate = this.atZone(ZoneId.systemDefault()).toLocalDate()

fun Instant.toLocalDateTime(): LocalDateTime = this.atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Instant.toZonedDateTime(): ZonedDateTime = this.atZone(ZoneId.systemDefault())

fun LocalDate.startOfWeek(): LocalDate = minusDays(dayOfWeek.value - 1L)

fun LocalDate.startOfMonth(): LocalDate = minusDays(dayOfMonth - 1L)
