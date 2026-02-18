package com.hansholz.bestenotenapp.screens.biometry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.security.kSafe
import kotlinx.coroutines.launch

class BiometryViewModel : ViewModel() {
    var isFailure by mutableStateOf(false)

    val kSafe = kSafe()

    fun tryBiometricAuthentication(
        viewModel: com.hansholz.bestenotenapp.main.ViewModel,
        onNavigateToScreen: (Screen) -> Unit,
    ) {
        if (listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform())) {
            kSafe.verifyBiometricDirect("Authentifiziere dich, um Einblicke in deine Noten zu erhalten.") { isSuccessful ->
                viewModelScope.launch {
                    if (isSuccessful) {
                        onNavigateToScreen(
                            if (kSafe.getDirect("authToken", "").isEmpty()) {
                                Screen.Login
                            } else {
                                Screen.Main
                            },
                        )
                    } else {
                        isFailure = true
                    }
                }
            }
        } else {
            viewModel.logout()
            onNavigateToScreen(Screen.Login)
        }
    }
}
