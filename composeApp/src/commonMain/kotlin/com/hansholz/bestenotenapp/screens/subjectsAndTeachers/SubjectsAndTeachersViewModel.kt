package com.hansholz.bestenotenapp.screens.subjectsAndTeachers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SubjectsAndTeachersViewModel(
    viewModel: com.hansholz.bestenotenapp.main.ViewModel,
) : ViewModel() {
    var isLoading by mutableStateOf(false)

    var topPadding by mutableStateOf(0.dp)

    init {
        viewModelScope.launch {
            isLoading = true
            if (viewModel.subjectsAndTeachers.isEmpty()) {
                viewModel.getSubjectsAndTeachers()?.let { viewModel.subjectsAndTeachers.addAll(it) }
            }
            if (viewModel.teachersAndSubjects.isEmpty()) {
                viewModel.getTeachersAndSubjects()?.let { viewModel.teachersAndSubjects.addAll(it) }
            }
            if (viewModel.subjects.isEmpty()) {
                viewModel.getSubjects()?.let { viewModel.subjects.addAll(it) }
            }
            isLoading = false
        }
    }
}
