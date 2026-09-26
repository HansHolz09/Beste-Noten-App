package com.hansholz.bestenotenapp.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Content_paste
import com.composables.icons.materialsymbols.rounded.Login
import com.composables.icons.materialsymbols.rounded.Visibility
import com.composables.icons.materialsymbols.rounded.Visibility_off
import com.hansholz.bestenotenapp.api.ManagedPersonalAccessToken
import com.hansholz.bestenotenapp.api.PasswordLoginStep
import com.hansholz.bestenotenapp.components.CurvedText
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedTextButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.keepKeyboardOnPress
import com.hansholz.bestenotenapp.components.nativeTextInputTint
import com.hansholz.bestenotenapp.components.rotateForever
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.LocalBiometricAuthenticationAvailable
import com.hansholz.bestenotenapp.main.LocalNativeKeyboardHandoff
import com.hansholz.bestenotenapp.main.LocalNativeTextInputOptions
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.LocalTimetableBlockViewEnabled
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import dev.chrisbanes.haze.hazeSource
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Login(
    viewModel: ViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToGrades: () -> Unit,
) = kSafeProviderCompose(viewModel.kSafe) {
    val loginViewModel = viewModel { LoginViewModel() }

    val scope = viewModel.viewModelScope
    val focusScope = rememberCoroutineScope()

    val vibrator = rememberVibrator()

    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    val animationsEnabled by LocalAnimationsEnabled.current
    var timetableBlockViewEnabled by LocalTimetableBlockViewEnabled.current
    var requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val nativeKeyboardHandoff = LocalNativeKeyboardHandoff.current

    var stayLoggedIn by rememberSaveable { mutableStateOf(false) }
    var otherOptions by rememberSaveable { mutableStateOf(getPlatform() == Platform.WEB) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf(TextFieldValue()) }
    val passwordFocus = remember { FocusRequester() }
    val codeFocus = remember { FocusRequester() }
    var identifierFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    fun moveToPassword() {
        if (nativeKeyboardHandoff != null) {
            nativeKeyboardHandoff { passwordFocus.requestFocus() }
        } else {
            focusScope.launch {
                withFrameNanos {}
                passwordFocus.requestFocus()
            }
        }
    }

    LaunchedEffect(loginViewModel.twoFactorRequired) {
        if (loginViewModel.twoFactorRequired) {
            withFrameNanos {}
            codeFocus.requestFocus()
        }
    }

    suspend fun runLogin(handleToken: suspend () -> Unit): Boolean =
        viewModel.login(
            stayLoggedIn = stayLoggedIn,
            isLoading = { loginViewModel.isLoading = it },
            applySettings = { timetableBlockViewEnabled = it },
            onNavigateHome = onNavigateHome,
            chooseStudent = { students, callback ->
                loginViewModel.chooseStudentDialog = true to students
                while (loginViewModel.chosenStudent == null) delay(100.milliseconds)
                callback(loginViewModel.chosenStudent!!)
            },
            handleToken = handleToken,
        )

    suspend fun completeNativeLogin(credential: ManagedPersonalAccessToken) {
        val success = runLogin { viewModel.acceptManagedPat(credential) }
        if (!success) viewModel.discardManagedPat(credential)
        loginViewModel.resetPasswordLogin()
    }

    fun submitNativeLogin() {
        if (loginViewModel.isSubmitting) return
        scope.launch {
            loginViewModel.isSubmitting = true
            loginViewModel.loginError = null
            try {
                val step =
                    if (loginViewModel.twoFactorRequired) {
                        loginViewModel.passwordLogin.submitCode(code.text)
                    } else {
                        loginViewModel.passwordLogin.submitCredentials(identifier.trim(), password)
                    }
                when (step) {
                    PasswordLoginStep.TwoFactorRequired -> {
                        password = ""
                        code = TextFieldValue()
                        loginViewModel.twoFactorRequired = true
                    }

                    is PasswordLoginStep.Complete -> {
                        password = ""
                        completeNativeLogin(step.credential)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                loginViewModel.loginError = "Die Anmeldung ist fehlgeschlagen. Bitte versuche es erneut."
            } finally {
                loginViewModel.isSubmitting = false
            }
        }
    }

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
            EnhancedAnimatedContent(
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
                                fontFamily = FontFamilies.Schoolbell,
                                textAlign = TextAlign.Center,
                                style = typography.headlineMedium,
                            )
                            Spacer(Modifier.height(30.dp))
                            EnhancedAnimatedContent(
                                targetState = otherOptions,
                                modifier = modifier,
                                contentAlignment = Alignment.Center,
                            ) { showOtherOptions ->
                                if (showOtherOptions) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        val textFieldState = rememberTextFieldState()
                                        OutlinedTextField(
                                            state = textFieldState,
                                            modifier = Modifier.fillMaxWidth().nativeTextInputTint(colorScheme.primary),
                                            leadingIcon = {
                                                EnhancedIconButton(onClick = {
                                                    scope.launch { textFieldState.setTextAndPlaceCursorAtEnd(clipboard.getText()?.text ?: "") }
                                                }) {
                                                    Icon(MaterialSymbols.Rounded.Content_paste, null)
                                                }
                                            },
                                            trailingIcon = {
                                                EnhancedIconButton(
                                                    onClick = { scope.launch { runLogin { viewModel.authToken.value = textFieldState.text.toString() } } },
                                                    enabled = textFieldState.text.isNotEmpty(),
                                                ) {
                                                    Icon(MaterialSymbols.Rounded.Login, null)
                                                }
                                            },
                                            placeholder = { Text("Private-Access-Token") },
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, platformImeOptions = LocalNativeTextInputOptions.current),
                                            onKeyboardAction =
                                                KeyboardActionHandler {
                                                    scope.launch { runLogin { viewModel.authToken.value = textFieldState.text.toString() } }
                                                },
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            shape = RoundedCornerShape(18.dp),
                                        )
                                        Text("oder")
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    runLogin {
                                                        if (!viewModel.getAccessToken()) error("Could not get Token")
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text("Login über beste.schule")
                                        }
                                        EnhancedButton(
                                            onClick = { scope.launch { viewModel.openGradesFromJson(onNavigateToGrades) } },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text("Noten aus JSON öffnen")
                                        }
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    viewModel.loginDemo(
                                                        isLoading = { loginViewModel.isLoading = it },
                                                        applySettings = { timetableBlockViewEnabled = it },
                                                        onNavigateHome = onNavigateHome,
                                                    )
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text("Demo-Account nutzen")
                                        }
                                    }
                                } else {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        OutlinedTextField(
                                            value = identifier,
                                            onValueChange = { identifier = it },
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .onFocusChanged { identifierFocused = it.isFocused }
                                                    .keepKeyboardOnPress(passwordFocused)
                                                    .onPreviewKeyEvent {
                                                        if (getPlatform() == Platform.IOS && it.key == Key.Tab) {
                                                            if (it.type == KeyEventType.KeyDown) {
                                                                moveToPassword()
                                                            }
                                                            true
                                                        } else {
                                                            false
                                                        }
                                                    }.nativeTextInputTint(colorScheme.primary),
                                            label = { Text("E-Mail oder Nutzername") },
                                            singleLine = true,
                                            enabled = !loginViewModel.isSubmitting && !loginViewModel.twoFactorRequired,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, platformImeOptions = LocalNativeTextInputOptions.current),
                                            keyboardActions = KeyboardActions(onNext = { moveToPassword() }),
                                        )
                                        OutlinedTextField(
                                            value = password,
                                            onValueChange = { password = it },
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .focusRequester(passwordFocus)
                                                    .onFocusChanged { passwordFocused = it.isFocused }
                                                    .keepKeyboardOnPress(identifierFocused)
                                                    .nativeTextInputTint(colorScheme.primary),
                                            label = { Text("Passwort") },
                                            trailingIcon = {
                                                EnhancedIconButton(
                                                    onClick = { passwordVisible = !passwordVisible },
                                                    enabled = !loginViewModel.isSubmitting && !loginViewModel.twoFactorRequired,
                                                ) {
                                                    EnhancedAnimatedContent(passwordVisible) { visible ->
                                                        Icon(if (visible) MaterialSymbols.Rounded.Visibility_off else MaterialSymbols.Rounded.Visibility, null)
                                                    }
                                                }
                                            },
                                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            singleLine = true,
                                            enabled = !loginViewModel.isSubmitting && !loginViewModel.twoFactorRequired,
                                            keyboardOptions =
                                                KeyboardOptions(
                                                    keyboardType = KeyboardType.Password,
                                                    imeAction = ImeAction.Done,
                                                    platformImeOptions = LocalNativeTextInputOptions.current,
                                                ),
                                            keyboardActions = KeyboardActions(onDone = { submitNativeLogin() }),
                                        )
                                        EnhancedAnimatedVisibility(loginViewModel.twoFactorRequired) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(
                                                    text = "Code aus deiner Authenticator-App",
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center,
                                                )
                                                BasicTextField(
                                                    value = code,
                                                    onValueChange = { value ->
                                                        val digits = value.text.filter(Char::isDigit).take(6)
                                                        code = if (digits == value.text) value else TextFieldValue(digits, selection = TextRange(digits.length))
                                                    },
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(56.dp)
                                                            .focusRequester(codeFocus)
                                                            .nativeTextInputTint(Color.Transparent),
                                                    enabled = !loginViewModel.isSubmitting,
                                                    singleLine = true,
                                                    textStyle = typography.titleLarge.copy(color = Color.Transparent),
                                                    cursorBrush = SolidColor(Color.Transparent),
                                                    keyboardOptions =
                                                        KeyboardOptions(
                                                            keyboardType = KeyboardType.NumberPassword,
                                                            imeAction = ImeAction.Done,
                                                            platformImeOptions = LocalNativeTextInputOptions.current,
                                                        ),
                                                    keyboardActions = KeyboardActions(onDone = { submitNativeLogin() }),
                                                    decorationBox = { innerTextField ->
                                                        Box {
                                                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                                repeat(6) { index ->
                                                                    Box(
                                                                        modifier =
                                                                            Modifier
                                                                                .weight(1f)
                                                                                .fillMaxHeight()
                                                                                .border(
                                                                                    1.dp,
                                                                                    if (index == code.text.length) colorScheme.primary else colorScheme.outline,
                                                                                    RoundedCornerShape(12.dp),
                                                                                ),
                                                                        contentAlignment = Alignment.Center,
                                                                    ) {
                                                                        Text(
                                                                            code.text
                                                                                .getOrNull(index)
                                                                                ?.toString()
                                                                                .orEmpty(),
                                                                            style = typography.titleLarge,
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            Box(Modifier.matchParentSize()) { innerTextField() }
                                                        }
                                                    },
                                                )
                                                EnhancedTextButton(
                                                    onClick = {
                                                        val digits =
                                                            clipboard
                                                                .getText()
                                                                ?.text
                                                                .orEmpty()
                                                                .filter(Char::isDigit)
                                                        if (digits.length == 6) {
                                                            code = TextFieldValue(digits, selection = TextRange(digits.length))
                                                            loginViewModel.loginError = null
                                                        } else {
                                                            loginViewModel.loginError = "Die Zwischenablage enthält keinen sechsstelligen Code."
                                                        }
                                                    },
                                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                                    enabled = !loginViewModel.isSubmitting,
                                                ) {
                                                    Icon(MaterialSymbols.Rounded.Content_paste, null)
                                                    Spacer(Modifier.width(8.dp))
                                                    Text("Code einfügen")
                                                }
                                            }
                                        }
                                        EnhancedAnimatedVisibility(loginViewModel.loginError != null) {
                                            Text(
                                                text = loginViewModel.loginError.orEmpty(),
                                                modifier = Modifier.fillMaxWidth(),
                                                color = colorScheme.error,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        EnhancedButton(
                                            onClick = ::submitNativeLogin,
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled =
                                                !loginViewModel.isSubmitting &&
                                                    if (loginViewModel.twoFactorRequired) code.text.length == 6 else identifier.isNotBlank() && password.isNotBlank(),
                                        ) {
                                            EnhancedAnimatedContent(
                                                if (loginViewModel.isSubmitting) {
                                                    "Bitte warten…"
                                                } else if (loginViewModel.twoFactorRequired) {
                                                    "Bestätigen"
                                                } else {
                                                    "Anmelden"
                                                },
                                            ) { Text(it) }
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(modifier.padding(top = 10.dp))
                            Row(
                                modifier =
                                    modifier
                                        .clip(shapes.medium)
                                        .clickable {
                                            val newValue = !stayLoggedIn
                                            stayLoggedIn = newValue
                                            vibrator.enhancedVibrate(
                                                if (newValue) {
                                                    EnhancedVibrations.TOGGLE_ON
                                                } else {
                                                    EnhancedVibrations.TOGGLE_OFF
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
                                )
                            }
                            if (LocalBiometricAuthenticationAvailable.current) {
                                Row(
                                    modifier =
                                        modifier
                                            .clip(shapes.medium)
                                            .clickable {
                                                val newValue = !requireBiometricAuthentification
                                                if (newValue) {
                                                    KSafeBiometrics.verifyBiometricDirect(
                                                        (if (getExactPlatform() == ExactPlatform.MACOS) "eine Bestätigung" else "Bestätige") +
                                                            ", um die biometrische Authentifizierung beim Start zu aktivieren",
                                                    ) { isSuccessful ->
                                                        if (isSuccessful) {
                                                            requireBiometricAuthentification = newValue
                                                            putSecure("requireBiometricAuthentification", newValue)
                                                        }
                                                    }
                                                } else {
                                                    requireBiometricAuthentification = newValue
                                                    putSecure("requireBiometricAuthentification", newValue)
                                                }
                                                vibrator.enhancedVibrate(
                                                    if (newValue) {
                                                        EnhancedVibrations.TOGGLE_ON
                                                    } else {
                                                        EnhancedVibrations.TOGGLE_OFF
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
                                                KSafeBiometrics.verifyBiometricDirect("Bestätige, um die biometrische Authentifizierung beim Start zu aktiven.") { isSuccessful ->
                                                    if (isSuccessful) {
                                                        requireBiometricAuthentification = it
                                                        putSecure("requireBiometricAuthentification", it)
                                                    }
                                                }
                                            } else {
                                                requireBiometricAuthentification = it
                                                putSecure("requireBiometricAuthentification", it)
                                            }
                                        },
                                    )
                                }
                            }
                            if (getPlatform() != Platform.WEB) {
                                EnhancedAnimatedContent(otherOptions) {
                                    EnhancedTextButton(
                                        onClick = {
                                            otherOptions = !it
                                            loginViewModel.resetPasswordLogin()
                                            password = ""
                                            code = TextFieldValue()
                                        },
                                        modifier = modifier,
                                    ) {
                                        Text(if (it) "Direkte Anmeldung" else "Weitere Optionen")
                                    }
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
