package com.hansholz.bestenotenapp.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthentication

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometryAuthenticator {
    private val context = LAContext()

    @OptIn(ExperimentalForeignApi::class)
    actual fun checkBiometryAuthentication(
        requestTitle: String,
        requestReason: String,
        scope: CoroutineScope,
        result: (successful: Boolean) -> Unit
    ) {
        scope.launch {
            if (isBiometricAvailable()) {
                context.evaluatePolicy(
                    policy = LAPolicyDeviceOwnerAuthentication,
                    localizedReason = requestReason,
                ) { success, error ->
                    result(success)
                }
            } else {
                result(false)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun isBiometricAvailable(): Boolean {
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)
    }
}