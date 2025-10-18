package com.hansholz.bestenotenapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    viewModel: com.hansholz.bestenotenapp.main.ViewModel,
) : ViewModel() {
    private val settings = Settings()

    var isGradesLoading by mutableStateOf(false)
    var isTimetableLoading by mutableStateOf(false)

    fun refreshGrades(viewModel: com.hansholz.bestenotenapp.main.ViewModel) {
        viewModelScope.launch {
            isGradesLoading = true
            viewModel.getCollections()?.let {
                viewModel.startGradeCollections.clear()
                viewModel.startGradeCollections.addAll(it)
            }
            isGradesLoading = false
        }
    }

    fun refreshTimetable(viewModel: com.hansholz.bestenotenapp.main.ViewModel) {
        viewModelScope.launch {
            isTimetableLoading = true
            @OptIn(ExperimentalTime::class)
            val currentDate =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .let {
                        "${it.year}-${it.month.number.toString().padStart(2, '0')}" +
                            "-${it.day.toString().padStart(2, '0')}"
                    }
            viewModel.currentJournalDay.value = viewModel.getJournalWeek(useCached = false)?.days?.find { it.date == currentDate }
            isTimetableLoading = false
        }
    }

    init {
        viewModelScope.launch {
            if (settings.getBoolean("showNewestGrades", true)) {
                isGradesLoading = true
                if (viewModel.startGradeCollections.isEmpty()) {
                    viewModel.getCollections()?.let { viewModel.startGradeCollections.addAll(it) }
                }
                isGradesLoading = false
            }
        }
        viewModelScope.launch {
            if (settings.getBoolean("showCurrentLesson", true)) {
                isTimetableLoading = true
                if (viewModel.currentJournalDay.value == null) {
                    @OptIn(ExperimentalTime::class)
                    val currentDate =
                        Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                            .let {
                                "${it.year}-${it.month.number.toString().padStart(2, '0')}" +
                                    "-${it.day.toString().padStart(2, '0')}"
                            }
                    viewModel.currentJournalDay.value = viewModel.getJournalWeek()?.days?.find { it.date == currentDate }
                }
                isTimetableLoading = false
            }
        }
    }
}
