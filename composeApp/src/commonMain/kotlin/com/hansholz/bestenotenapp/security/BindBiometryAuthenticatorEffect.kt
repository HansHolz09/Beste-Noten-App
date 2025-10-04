package com.hansholz.bestenotenapp.security

import androidx.compose.runtime.Composable

@Composable
expect fun BindBiometryAuthenticatorEffect(
    biometryAuthenticator: BiometryAuthenticator,
    onBinded: suspend () -> Unit = {},
)
