package com.hansholz.bestenotenapp.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hansholz.bestenotenapp.utils.AndroidContext
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BiometryAuthenticator {
    private var fragmentManager: FragmentManager? = null

    fun bind(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }

    actual fun checkBiometryAuthentication(
        requestTitle: String,
        requestReason: String,
        scope: CoroutineScope,
        result: (successful: Boolean) -> Unit
    ) {
        val resolverFragment: ResolverFragment = getResolverFragment()

        scope.launch {
            resolverFragment.showBiometricPrompt(
                requestTitle = requestTitle,
                requestReason = requestReason,
            ) {
                result(it)
            }
        }
    }

    actual fun isBiometricAvailable(): Boolean {
        val manager: BiometricManager = BiometricManager.from(AndroidContext.context.get()!!)
        return manager.canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun getResolverFragment(): ResolverFragment {
        val fragmentManager: FragmentManager = fragmentManager
            ?: error("can't check biometry without active window")

        val currentFragment: Fragment? = fragmentManager
            .findFragmentByTag(BIOMETRY_RESOLVER_FRAGMENT_TAG)

        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, BIOMETRY_RESOLVER_FRAGMENT_TAG)
                    .commitNow()
            }
        }
    }

    class ResolverFragment : Fragment() {
        private lateinit var executor: Executor
        private lateinit var biometricPrompt: BiometricPrompt
        private lateinit var promptInfo: BiometricPrompt.PromptInfo

        fun showBiometricPrompt(
            requestTitle: String,
            requestReason: String,
            callback: (Boolean) -> Unit
        ) {
            val context = AndroidContext.context.get()!!

            executor = ContextCompat.getMainExecutor(context)

            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        callback(false)
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        callback(true)
                    }
                }
            )

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(requestTitle)
                .setSubtitle(requestReason)
                .setNegativeButtonText("Abbrechen")
                .setConfirmationRequired(false)
                .setAllowedAuthenticators(BIOMETRIC_WEAK)
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }

    companion object {
        private const val BIOMETRY_RESOLVER_FRAGMENT_TAG = "BiometryControllerResolver"
    }
}