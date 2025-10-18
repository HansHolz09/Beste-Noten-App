package com.hansholz.bestenotenapp.screens.biometry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.security.BiometryAuthenticator
import kotlinx.coroutines.launch

class BiometryViewModel : ViewModel() {
    var isFailure by mutableStateOf(false)

    val biometryAuthenticator = BiometryAuthenticator()

    fun tryBiometricAuthentication(
        viewModel: com.hansholz.bestenotenapp.main.ViewModel,
        onNavigateToScreen: (Screen) -> Unit,
    ) {
        if (biometryAuthenticator.isBiometricAvailable()) {
            biometryAuthenticator.checkBiometryAuthentication(
                requestTitle = "Authentifizieren",
                requestReason = "Authentifiziere dich, um Einblicke in deine Noten zu erhalten.",
                scope = viewModelScope,
            ) { isSuccessful ->
                viewModelScope.launch {
                    if (isSuccessful) {
                        onNavigateToScreen(
                            if (viewModel.authTokenManager.getToken().isNullOrEmpty()) {
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
