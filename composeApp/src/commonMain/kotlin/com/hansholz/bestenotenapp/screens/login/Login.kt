package com.hansholz.bestenotenapp.screens.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.hansholz.bestenotenapp.components.CurvedText
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.rotateForever
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.security.BindBiometryAuthenticatorEffect
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Login(
    viewModel: ViewModel,
    onNavigateHome: () -> Unit,
) {
    val loginViewModel = viewModel { LoginViewModel() }

    val scope = viewModel.viewModelScope

    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val animationsEnabled by LocalAnimationsEnabled.current
    var requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val settings = Settings()

    BindBiometryAuthenticatorEffect(loginViewModel.biometryAuthenticator)

    TopAppBarScaffold(
        title = "Login",
        hazeState = viewModel.hazeBackgroundState3,
    ) { innerPadding, topAppBarBackground ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().hazeSource(viewModel.hazeBackgroundState3),
            contentAlignment = Alignment.Center,
        ) {
            val width = this.maxWidth
            val modifier = if (width >= 780.dp) Modifier.width(400.dp) else Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            AnimatedContent(
                targetState = loginViewModel.isLoading,
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) { targetState ->
                if (targetState) {
                    ContainedLoadingIndicator(Modifier.padding(innerPadding))
                } else {
                    Box(Modifier.verticalScroll(rememberScrollState())) {
                        Column(
                            modifier =
                                Modifier
                                    .padding(vertical = 20.dp)
                                    .padding(innerPadding)
                                    .consumeWindowInsets(innerPadding)
                                    .imePadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            var stayLoggedIn by rememberSaveable { mutableStateOf(false) }

                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(Res.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(150.dp),
                                )
                                CurvedText(
                                    text = "Willkommen bei der Beste-Noten-App",
                                    radius = 100.dp,
                                    modifier = Modifier.size(225.dp).rotateForever(10000, false, animationsEnabled),
                                    textStyle = typography.headlineLarge,
                                    startAngle = 110f,
                                    sweepAngle = 315f,
                                )
                            }
                            Spacer(Modifier.height(30.dp))
                            Text(
                                text = "Melde dich mit deinem Account von beste.schule an",
                                modifier = modifier,
                                fontFamily = FontFamilies.Schoolbell(),
                                textAlign = TextAlign.Center,
                                style = typography.headlineMedium,
                            )
                            Spacer(Modifier.height(30.dp))
                            val textFieldState = rememberTextFieldState()
                            OutlinedTextField(
                                state = textFieldState,
                                modifier = modifier.widthIn(max = 500.dp),
                                leadingIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            scope.launch {
                                                textFieldState.setTextAndPlaceCursorAtEnd(clipboard.getText()?.text ?: "")
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Outlined.ContentPaste, null)
                                    }
                                },
                                trailingIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            scope.launch {
                                                viewModel.login(
                                                    stayLoggedIn = stayLoggedIn,
                                                    isLoading = { loginViewModel.isLoading = it },
                                                    onNavigateHome = onNavigateHome,
                                                    chooseStudent = { students, callback ->
                                                        loginViewModel.chooseStudentDialog = true to students
                                                        while (loginViewModel.chosenStudent == null) {
                                                            delay(100)
                                                        }
                                                        callback(loginViewModel.chosenStudent!!)
                                                    },
                                                ) {
                                                    viewModel.authToken.value = textFieldState.text.toString()
                                                }
                                            }
                                        },
                                        enabled = textFieldState.text.isNotEmpty(),
                                    ) {
                                        Icon(Icons.AutoMirrored.Outlined.Login, null)
                                    }
                                },
                                placeholder = { Text("Private-Access-Token") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                onKeyboardAction =
                                    KeyboardActionHandler {
                                        scope.launch {
                                            viewModel.login(
                                                stayLoggedIn = stayLoggedIn,
                                                isLoading = { loginViewModel.isLoading = it },
                                                onNavigateHome = onNavigateHome,
                                                chooseStudent = { students, callback ->
                                                    loginViewModel.chooseStudentDialog = true to students
                                                    while (loginViewModel.chosenStudent == null) {
                                                        delay(100)
                                                    }
                                                    callback(loginViewModel.chosenStudent!!)
                                                },
                                            ) {
                                                viewModel.authToken.value = textFieldState.text.toString()
                                            }
                                        }
                                    },
                                lineLimits = TextFieldLineLimits.SingleLine,
                            )
                            Text("oder")
                            EnhancedButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.login(
                                            stayLoggedIn = stayLoggedIn,
                                            isLoading = { loginViewModel.isLoading = it },
                                            onNavigateHome = onNavigateHome,
                                            chooseStudent = { students, callback ->
                                                loginViewModel.chooseStudentDialog = true to students
                                                while (loginViewModel.chosenStudent == null) {
                                                    delay(100)
                                                }
                                                callback(loginViewModel.chosenStudent!!)
                                            },
                                        ) {
                                            val successful = viewModel.getAccessToken()
                                            if (!successful) error("Could not get Token")
                                        }
                                    }
                                },
                                modifier = modifier,
                            ) {
                                Text("Login über beste.schule")
                            }
                            Text("oder")
                            EnhancedButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.loginDemo(
                                            isLoading = { loginViewModel.isLoading = it },
                                            onNavigateHome = onNavigateHome,
                                        )
                                    }
                                },
                                modifier = modifier,
                            ) {
                                Text("Demo-Account nutzen")
                            }
                            HorizontalDivider(modifier.padding(top = 10.dp))
                            Row(
                                modifier =
                                    modifier
                                        .clip(shapes.medium)
                                        .clickable(viewModel.authTokenManager.isAvailable) {
                                            val newValue = !stayLoggedIn
                                            stayLoggedIn = newValue
                                            hapticFeedback.performHapticFeedback(
                                                if (newValue) {
                                                    HapticFeedbackType.ToggleOn
                                                } else {
                                                    HapticFeedbackType.ToggleOff
                                                },
                                            )
                                        }.padding(start = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Angemeldet bleiben",
                                    modifier = Modifier.weight(1f),
                                    style = typography.bodyLarge,
                                )
                                EnhancedCheckbox(
                                    checked = stayLoggedIn,
                                    onCheckedChange = { stayLoggedIn = it },
                                    enabled = viewModel.authTokenManager.isAvailable,
                                )
                            }
                            if (loginViewModel.biometryAuthenticator.isBiometricAvailable()) {
                                Row(
                                    modifier =
                                        modifier
                                            .clip(shapes.medium)
                                            .clickable {
                                                val newValue = !requireBiometricAuthentification
                                                if (newValue) {
                                                    loginViewModel.biometryAuthenticator.checkBiometryAuthentication(
                                                        requestTitle = "Bestätigen",
                                                        requestReason = "Bestätige, um die biometrische Authentifizierung beim Start zu aktiven",
                                                        scope = scope,
                                                    ) { isSuccessful ->
                                                        if (isSuccessful) {
                                                            requireBiometricAuthentification = newValue
                                                            settings["requireBiometricAuthentification"] = newValue
                                                        }
                                                    }
                                                } else {
                                                    requireBiometricAuthentification = newValue
                                                    settings["requireBiometricAuthentification"] = newValue
                                                }
                                                hapticFeedback.performHapticFeedback(
                                                    if (newValue) {
                                                        HapticFeedbackType.ToggleOn
                                                    } else {
                                                        HapticFeedbackType.ToggleOff
                                                    },
                                                )
                                            }.padding(start = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Biometrische Authentifizierung erforderlich",
                                        modifier = Modifier.weight(1f),
                                        style = typography.bodyLarge,
                                    )
                                    EnhancedCheckbox(
                                        checked = requireBiometricAuthentification,
                                        onCheckedChange = {
                                            if (it) {
                                                loginViewModel.biometryAuthenticator.checkBiometryAuthentication(
                                                    requestTitle = "Bestätigen",
                                                    requestReason = "Bestätige, um die biometrische Authentifizierung beim Start zu aktiven",
                                                    scope = scope,
                                                ) { isSuccessful ->
                                                    if (isSuccessful) {
                                                        requireBiometricAuthentification = it
                                                        settings["requireBiometricAuthentification"] = it
                                                    }
                                                }
                                            } else {
                                                requireBiometricAuthentification = it
                                                settings["requireBiometricAuthentification"] = it
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        topAppBarBackground(innerPadding.calculateTopPadding())
    }

    ChooseStudentDialog(loginViewModel)
}
