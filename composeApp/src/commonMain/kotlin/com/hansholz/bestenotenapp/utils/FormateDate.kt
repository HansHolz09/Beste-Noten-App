package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun parseDateOrNull(date: String?): LocalDate? = date?.let { runCatching { LocalDate.parse(it.take(10)) }.getOrNull() }

fun formateDate(date: String): String = parseDateOrNull(date)?.let { "${it.day}.${it.month.number}.${it.year}" } ?: "Unbekanntes Datum"
