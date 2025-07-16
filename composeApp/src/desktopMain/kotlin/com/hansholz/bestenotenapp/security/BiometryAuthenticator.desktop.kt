package com.hansholz.bestenotenapp.security

import kotlinx.coroutines.CoroutineScope

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometryAuthenticator {
    actual fun checkBiometryAuthentication(
        requestTitle: String,
        requestReason: String,
        scope: CoroutineScope,
        result: (Boolean) -> Unit
    ) {
        error("Not supported")
    }

    actual fun isBiometricAvailable(): Boolean = false
}