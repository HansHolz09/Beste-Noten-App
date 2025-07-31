package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

val LocalDate.weekOfYear: Int
    get() {
        val thursday = this.plus(4 - this.dayOfWeek.isoDayNumber, DateTimeUnit.DAY)
        val firstThursday = LocalDate(thursday.year, 1, 4)
            .plus(4 - LocalDate(thursday.year, 1, 4).dayOfWeek.isoDayNumber, DateTimeUnit.DAY)

        return ((thursday.dayOfYear - firstThursday.dayOfYear) / 7) + 1
    }