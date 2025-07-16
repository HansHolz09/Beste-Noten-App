package com.hansholz.bestenotenapp.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun BindBiometryAuthenticatorEffect(
    biometryAuthenticator: BiometryAuthenticator,
    onBinded: suspend () -> Unit
) {
    LaunchedEffect(Unit) {
        onBinded()
    }
}