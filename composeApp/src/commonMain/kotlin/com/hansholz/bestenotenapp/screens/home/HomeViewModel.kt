package com.hansholz.bestenotenapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.security.kSafeProvider
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import com.hansholz.bestenotenapp.main.ViewModel as AppViewModel

class HomeViewModel(
    viewModel: AppViewModel,
) : ViewModel() {
    var isGradesLoading by mutableStateOf(false)
    var isTimetableLoading by mutableStateOf(false)
    var isStatsLoading by mutableStateOf(false)

    var isStatsDialogShown by mutableStateOf(false)
    var isStatsDialogLoading by mutableStateOf(true)
    var isTimesDialogShown by mutableStateOf(false)
    var isTimesDialogLoading by mutableStateOf(true)
    var isAccountSchoolDialogShown by mutableStateOf(false)
    var isYearSelectionDialogShown by mutableStateOf(false)
    var isYearSelectionDialogLoading by mutableStateOf(true)

    fun refreshGrades(viewModel: AppViewModel) {
        viewModelScope.launch {
            isGradesLoading = true
            viewModel.getCollections()?.let {
                viewModel.startGradeCollections.clear()
                viewModel.startGradeCollections.addAll(it)
            }
            isGradesLoading = false
        }
    }

    fun refreshTimetable(viewModel: AppViewModel) {
        viewModelScope.launch {
            isTimetableLoading = true
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

    fun refreshStats(viewModel: AppViewModel) {
        viewModelScope.launch {
            isStatsLoading = true
            viewModel.getIntervals()?.let {
                viewModel.intervals.clear()
                viewModel.intervals.addAll(it)
            }
            isStatsLoading = false
        }
    }

    init {
        kSafeProvider(viewModel.kSafe) {
            viewModelScope.launch {
                if (get("showNewestGrades", true)) {
                    isGradesLoading = true
                    if (viewModel.startGradeCollections.isEmpty()) {
                        viewModel.getCollections()?.let { viewModel.startGradeCollections.addAll(it) }
                    }
                    isGradesLoading = false
                }
            }
            viewModelScope.launch {
                if (get("showCurrentLesson", true)) {
                    isTimetableLoading = true
                    if (viewModel.currentJournalDay.value == null) {
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
            viewModelScope.launch {
                if (get("showYearProgress", true)) {
                    isStatsLoading = true
                    if (viewModel.intervals.isEmpty()) {
                        viewModel.getIntervals()?.let { viewModel.intervals.addAll(it) }
                    }
                    isStatsLoading = false
                }
            }
        }
    }
}
