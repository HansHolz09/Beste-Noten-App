package com.hansholz.bestenotenapp.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hansholz.bestenotenapp.api.BesteSchulePasswordLogin
import com.hansholz.bestenotenapp.api.models.Student

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var isSubmitting by mutableStateOf(false)
    var twoFactorRequired by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    private var session: BesteSchulePasswordLogin? = null
    val passwordLogin: BesteSchulePasswordLogin
        get() = session ?: BesteSchulePasswordLogin().also { session = it }

    fun resetPasswordLogin() {
        session?.close()
        session = null
        twoFactorRequired = false
        loginError = null
    }

    var chooseStudentDialog by mutableStateOf<Pair<Boolean, List<Student>?>>(false to null)
    var chosenStudent by mutableStateOf<String?>(null)

    override fun onCleared() {
        resetPasswordLogin()
        super.onCleared()
    }
}
