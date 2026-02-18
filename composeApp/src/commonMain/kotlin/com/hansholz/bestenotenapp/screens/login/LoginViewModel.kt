package com.hansholz.bestenotenapp.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hansholz.bestenotenapp.api.models.Student

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)

    var chooseStudentDialog by mutableStateOf<Pair<Boolean, List<Student>?>>(false to null)
    var chosenStudent by mutableStateOf<String?>(null)
}
