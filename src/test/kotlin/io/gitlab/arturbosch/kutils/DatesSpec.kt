package io.gitlab.arturbosch.kutils

import io.kotlintest.matchers.date.shouldHaveSameDayAs
import io.kotlintest.specs.StringSpec
import java.time.LocalDate

class DatesSpec : StringSpec({

    "start of week" {
        LocalDate.of(2019, 6, 14).startOfWeek() shouldHaveSameDayAs LocalDate.of(2019, 6, 10)
    }

    "start of month" {
        LocalDate.of(2019, 6, 14).startOfMonth() shouldHaveSameDayAs LocalDate.of(2019, 6, 1)
    }
})
