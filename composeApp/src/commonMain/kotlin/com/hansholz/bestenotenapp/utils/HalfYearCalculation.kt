package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.Interval
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlin.time.Clock

fun schoolYearProgress(intervals: List<Interval>): Pair<Float, Float>? {
    if (intervals.size != 2) return null
    val periods =
        intervals
            .map {
                val from = parseDateOrNull(it.from) ?: return null
                val to = parseDateOrNull(it.to) ?: return null
                if (from > to) return null
                from to to
            }.sortedBy { it.first }
    val (firstStart, firstEnd) = periods[0]
    val (secondStart, secondEnd) = periods[1]
    if (firstEnd >= secondStart) return null
    return percentOfSchoolYearAt(firstStart, firstEnd, secondStart, secondEnd) to switchPercent(firstStart, firstEnd, secondStart, secondEnd)
}

fun switchPercent(
    firstStart: LocalDate,
    firstEnd: LocalDate,
    secondStart: LocalDate,
    secondEnd: LocalDate,
): Float {
    require(firstStart <= firstEnd)
    require(secondStart <= secondEnd)
    require(firstStart <= secondEnd)

    val totalDays = firstStart.until(secondEnd, DateTimeUnit.DAY).toFloat()
    if (totalDays == 0f) return 0.0f

    val firstEndPos = firstStart.until(firstEnd, DateTimeUnit.DAY).toFloat()
    val secondStartPos = firstStart.until(secondStart, DateTimeUnit.DAY).toFloat()
    val switchDate = ((firstEndPos + secondStartPos) / 2.0f).coerceIn(0.0f, totalDays)

    return switchDate / totalDays
}

fun percentOfSchoolYearAt(
    firstStart: LocalDate,
    firstEnd: LocalDate,
    secondStart: LocalDate,
    secondEnd: LocalDate,
    date: LocalDate =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date,
): Float {
    require(firstStart <= firstEnd)
    require(secondStart <= secondEnd)
    require(firstStart <= secondEnd)

    val totalDays = firstStart.until(secondEnd, DateTimeUnit.DAY).toFloat()
    if (totalDays == 0f) return 0.0f

    val d =
        when {
            date < firstStart -> firstStart
            date > secondEnd -> secondEnd
            else -> date
        }

    val elapsedDays = firstStart.until(d, DateTimeUnit.DAY).toFloat()
    return elapsedDays / totalDays
}
