package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TimetableViewModel : ViewModel() {
    val lessonPopupShown = mutableStateOf(false)

    var toolbarPadding by mutableStateOf(0.dp)

    @OptIn(ExperimentalTime::class)
    private val currentDate =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    @OptIn(ExperimentalTime::class)
    var startPageDate by mutableStateOf(
        when (currentDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> {
                currentDate.plus(DatePeriod(days = 2))
            }
            DayOfWeek.SUNDAY -> {
                currentDate.plus(DatePeriod(days = 1))
            }
            else -> {
                currentDate
            }
        },
    )
    var userScrollEnabled by mutableStateOf(true)
    var contentBlurred by mutableStateOf(false)

    var toolbarState by mutableStateOf(0)
}
