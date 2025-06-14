package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.LocalDate

fun formateDate(date: String): String =
    LocalDate.parse(date).let { "${it.dayOfMonth}.${it.monthNumber}.${it.year}" }