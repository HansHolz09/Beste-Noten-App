package com.hansholz.bestenotenapp.security

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

@Composable
actual fun BindBiometryAuthenticatorEffect(
    biometryAuthenticator: BiometryAuthenticator,
    onBinded: suspend () -> Unit,
) {
    val context: Context = LocalContext.current

    LaunchedEffect(biometryAuthenticator, context) {
        val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
        biometryAuthenticator.bind(fragmentManager)

        onBinded()
    }
}
