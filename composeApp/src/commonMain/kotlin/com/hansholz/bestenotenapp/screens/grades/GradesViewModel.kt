package com.hansholz.bestenotenapp.screens.grades

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.api.models.Year
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GradesViewModel(
    viewModel: com.hansholz.bestenotenapp.main.ViewModel,
) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    val selectedYears = mutableStateListOf<Year>()

    var topPadding by mutableStateOf(0.dp)
    var toolbarPadding by mutableStateOf(0.dp)

    var userScrollEnabled by mutableStateOf(true)
    var contentBlurred by mutableStateOf(false)

    var toolbarState by mutableStateOf(0)
    var analyzeYears by mutableStateOf(false)
    var filterSubjects by mutableStateOf(false)
    var filterShown by mutableStateOf(true)
    val deselectedSubjects = mutableStateListOf<String>()
    var titleHeight by mutableStateOf(0.dp)
    var closeBarHeight by mutableStateOf(0.dp)

    fun refreshGrades(viewModel: com.hansholz.bestenotenapp.main.ViewModel) {
        viewModelScope.launch {
            isLoading = true
            viewModel.gradeCollections.clear()
            if (viewModel.allGradeCollectionsLoaded.value) {
                viewModel.getCollections(viewModel.years)?.let {
                    viewModel.gradeCollections.addAll(it)
                    isLoading = false
                }
            } else {
                viewModel.getCollections(listOf(viewModel.years.last()))?.let {
                    viewModel.gradeCollections.addAll(it)
                    isLoading = false
                }
            }
        }
    }

    fun closeToolbar() {
        viewModelScope.launch {
            toolbarState = 0
            contentBlurred = false
            delay(250)
            if (toolbarState == 0) userScrollEnabled = true
        }
    }

    init {
        viewModelScope.launch {
            isLoading = true
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            if (viewModel.gradeCollections.isEmpty() && viewModel.years.isNotEmpty()) {
                viewModel.getCollections(listOf(viewModel.years.last()))?.let { viewModel.gradeCollections.addAll(it) }
            }
            if (viewModel.years.isNotEmpty()) {
                selectedYears.clear()
                selectedYears.add(viewModel.years.last())
            }
            isLoading = false
        }
    }
}
