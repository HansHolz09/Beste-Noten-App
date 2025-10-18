package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DayHeader(date: LocalDate) {
    fun getDayAbbreviation(dayOfWeek: DayOfWeek): String =
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mo"
            DayOfWeek.TUESDAY -> "Di"
            DayOfWeek.WEDNESDAY -> "Mi"
            DayOfWeek.THURSDAY -> "Do"
            DayOfWeek.FRIDAY -> "Fr"
            DayOfWeek.SATURDAY -> "Sa"
            DayOfWeek.SUNDAY -> "So"
        }

    val isCurrentDate = (
        date ==
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
    )
    val color = if (isCurrentDate) colorScheme.primary else null

    val dayAbbreviation = getDayAbbreviation(date.dayOfWeek)
    val formattedDate = "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = dayAbbreviation, color = color ?: Color.Unspecified, fontWeight = FontWeight.Bold)
        Text(text = formattedDate, color = color ?: Color.Gray)
    }
}
