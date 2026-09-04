package com.hansholz.bestenotenapp.screens.grades

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.utils.SecondaryStage
import com.hansholz.bestenotenapp.utils.secondaryStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import com.hansholz.bestenotenapp.main.ViewModel as AppViewModel

class GradesViewModel(
    viewModel: AppViewModel,
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    val selectedYears = mutableStateListOf<Year>()
    var selectedSecondaryStage by mutableStateOf<SecondaryStage?>(null)

    var topPadding by mutableStateOf(0.dp)
    var toolbarPadding by mutableStateOf(0.dp)

    var userScrollEnabled by mutableStateOf(true)
    var contentBlurred by mutableStateOf(false)
    var toolbarExpansionLocked by mutableStateOf(false)

    var toolbarState by mutableStateOf(0)
    var analyzeYears by mutableStateOf(false)
    var filterSubjects by mutableStateOf(false)
    var filterShown by mutableStateOf(true)
    val deselectedSubjects = mutableStateListOf<String>()
    var titleHeight by mutableStateOf(0.dp)
    var closeBarHeight by mutableStateOf(0.dp)

    fun refreshGrades(viewModel: AppViewModel) {
        viewModelScope.launch {
            isLoading = true
            try {
                if (viewModel.isDemoAccount.value) {
                    delay(1.seconds)
                    return@launch
                }
                if (viewModel.years.isEmpty()) {
                    viewModel.getYears()?.let { viewModel.years.addAll(it) }
                }
                if (viewModel.years.isEmpty()) return@launch
                viewModel.gradeCollections.clear()
                val collections =
                    if (viewModel.allGradeCollectionsLoaded.value) {
                        viewModel.getCollections(viewModel.years)
                    } else {
                        viewModel.getCollections()
                    }
                collections?.let {
                    viewModel.gradeCollections.addAll(it)
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun closeToolbar() {
        viewModelScope.launch {
            toolbarState = 0
            contentBlurred = false
            toolbarExpansionLocked = true
            delay(250.milliseconds)
            if (toolbarState == 0) userScrollEnabled = true
            delay(750.milliseconds)
            toolbarExpansionLocked = false
        }
    }

    init {
        viewModelScope.launch {
            isLoading = true
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            (
                viewModel.years.find {
                    it.id ==
                        viewModel.user.value
                            ?.config
                            ?.yearId
                } ?: viewModel.years.lastOrNull()
            )?.let { currentYear ->
                if (viewModel.gradeCollections.isEmpty()) {
                    viewModel.getCollections(listOf(currentYear))?.let { viewModel.gradeCollections.addAll(it) }
                }
                selectedYears.clear()
                selectedYears.add(currentYear)
                selectedSecondaryStage = viewModel.levelsByYear[currentYear.id]?.secondaryStage()
            }
            isLoading = false
        }
    }
}
