package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun formateDate(date: String): String =
    LocalDate.parse(date).let { "${it.day}.${it.month.number}.${it.year}" }