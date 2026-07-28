package com.hansholz.bestenotenapp.screens.biometry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.security.kSafeProvider
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import kotlinx.coroutines.launch

class BiometryViewModel : ViewModel() {
    var isFailure by mutableStateOf(false)

    val kSafe = kSafe()

    fun tryBiometricAuthentication(onNavigateToScreen: (Screen) -> Unit) =
        kSafeProvider(kSafe) {
            KSafeBiometrics.verifyBiometricDirect(
                (if (getExactPlatform() == ExactPlatform.MACOS) "eine Authentifizierung" else "Authentifiziere dich") +
                    ", um einen Einblick in deine Noten zu gestatten",
            ) { isSuccessful ->
                viewModelScope.launch {
                    if (isSuccessful) {
                        onNavigateToScreen(
                            if (get("authToken", "").isEmpty()) {
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
        }
}
