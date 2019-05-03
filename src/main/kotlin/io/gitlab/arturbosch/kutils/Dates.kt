package io.gitlab.arturbosch.kutils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())

fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

fun Date.toLocalDate(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun Date.toLocalDateTime(): LocalDateTime = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Date.toZonedDateTime(): ZonedDateTime = this.toInstant().atZone(ZoneId.systemDefault())
