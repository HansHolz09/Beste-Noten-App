package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.security.BindBiometryAuthenticatorEffect
import com.hansholz.bestenotenapp.security.BiometryAuthenticator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Biometry(
    viewModel: ViewModel,
    onNavigateToScreen: (Screen) -> Unit,
) {
    val scope = viewModel.viewModelScope

    var isFailure by remember { mutableStateOf(false) }

    val biometryAuthenticator = remember { BiometryAuthenticator() }

    fun tryBiometricAuthentication() {
        if (biometryAuthenticator.isBiometricAvailable()) {
            biometryAuthenticator.checkBiometryAuthentication(
                requestTitle = "Authentifizieren",
                requestReason = "Authentifiziere dich, um Einblicke in deine Noten zu erhalten.",
                scope = scope,
            ) { isSuccessful ->
                scope.launch {
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
    BindBiometryAuthenticatorEffect(biometryAuthenticator) {
        tryBiometricAuthentication()
    }

    TopAppBarScaffold(
        title = "Authentifizieren",
        hazeState = viewModel.hazeBackgroundState2,
    ) { innerPadding, topAppBarBackground ->
        EmptyStateMessage(
            title = "Gesperrt",
            button = {
                AnimatedVisibility(
                    visible = isFailure,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Column {
                        EnhancedButton(
                            onClick = {
                                tryBiometricAuthentication()
                            },
                            modifier = Modifier.sizeIn(maxWidth = 300.dp).fillMaxWidth().padding(10.dp),
                        ) {
                            Text("Erneut Versuchen")
                        }
                        EnhancedButton(
                            onClick = {
                                viewModel.logout()
                                onNavigateToScreen(Screen.Login)
                            },
                            modifier = Modifier.sizeIn(maxWidth = 300.dp).fillMaxWidth().padding(10.dp),
                        ) {
                            Text("Abmelden")
                        }
                    }
                }
            },
            modifier = Modifier.padding(innerPadding),
            icon = Icons.Outlined.Lock,
        )

        topAppBarBackground(innerPadding.calculateTopPadding())
    }
}
