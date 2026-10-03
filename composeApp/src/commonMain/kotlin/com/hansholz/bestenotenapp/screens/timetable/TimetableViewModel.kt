package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.security.kSafeProvider
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import com.hansholz.bestenotenapp.main.ViewModel as AppViewModel

class TimetableViewModel(
    viewModel: AppViewModel,
) : ViewModel() {
    var toolbarPadding by mutableStateOf(0.dp)

    var startPageDate by mutableStateOf(
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .let {
                when (it.dayOfWeek) {
                    DayOfWeek.SATURDAY -> it.plus(2, DateTimeUnit.DAY)
                    DayOfWeek.SUNDAY -> it.plus(1, DateTimeUnit.DAY)
                    else -> it
                }
            },
    )
    var userScrollEnabled by mutableStateOf(true)
    var contentBlurred by mutableStateOf(false)

    var toolbarState by mutableStateOf(0)

    init {
        viewModelScope.launch {
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            if (viewModel.absences.isEmpty() && kSafeProvider(viewModel.kSafe) { get("showAbsences", true) }) {
                viewModel.years.lastOrNull()?.let { currentYear ->
                    val currentYearId = currentYear.id.toString()
                    viewModel.getAbsences(currentYearId)?.let { viewModel.absences.add(currentYearId to it) }
                }
            }
        }
    }
}
