package com.hansholz.bestenotenapp.security

import kotlinx.coroutines.CoroutineScope

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class BiometryAuthenticator() {
    fun checkBiometryAuthentication(
        requestTitle: String,
        requestReason: String,
        scope: CoroutineScope,
        result: (successful: Boolean) -> Unit,
    )

    fun isBiometricAvailable(): Boolean
}
