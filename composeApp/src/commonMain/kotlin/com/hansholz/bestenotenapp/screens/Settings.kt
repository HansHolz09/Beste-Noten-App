@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DisabledVisible
import androidx.compose.material.icons.outlined.FiberNew
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Texture
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.components.PreferenceCategory
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.security.BindBiometryAuthenticatorEffect
import com.hansholz.bestenotenapp.security.BiometryAuthenticator
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalIsDark
import com.hansholz.bestenotenapp.theme.LocalSupportsCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseSystemIsDark
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Settings(
    viewModel: ViewModel,
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    TopAppBarScaffold(
        title = "Einstellungen",
        navigationIcon = {
            EnhancedIconButton(
                onClick = {
                    scope.launch {
                        viewModel.closeOrOpenDrawer(windowWithSizeClass)
                    }
                }
            ) {
                Icon(Icons.Filled.Menu, null)
            }
        },
        sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
        hazeState = viewModel.hazeBackgroundState
    ) { innerPadding, topAppBarBackground ->

        var useSystemIsDark by LocalUseSystemIsDark.current
        var isDark by LocalIsDark.current
        var useCustomColorScheme by LocalUseCustomColorScheme.current
        val supportsCustomColorScheme by LocalSupportsCustomColorScheme.current
        var animationsEnabled by LocalAnimationsEnabled.current
        var blurEnabled by LocalBlurEnabled.current
        var backgroundEnabled by LocalBackgroundEnabled.current
        var showGreetings by LocalShowGreetings.current
        var showNewestGrades by LocalShowNewestGrades.current
        var showGradeHistory by LocalShowGradeHistory.current
        var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
        var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
        var requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
        val settings = Settings()

        val biometryAuthenticator = remember { BiometryAuthenticator() }
        BindBiometryAuthenticatorEffect(biometryAuthenticator)

        LazyColumn(
            modifier = Modifier.hazeSource(viewModel.hazeBackgroundState),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                PreferenceCategory("Design", Modifier.padding(horizontal = 15.dp))
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Nachtmodus",
                    icon = Icons.Outlined.Brightness4,
                    position = PreferencePosition.Top,
                ) {
                    Row {
                        FilledIconToggleButton(
                            checked = useSystemIsDark,
                            onCheckedChange = {
                                useSystemIsDark = it
                                settings["useSystemIsDark"] = it
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            },
                            shapes = IconButtonDefaults.toggleableShapes()
                        ) {
                            Icon(Icons.Outlined.BrightnessAuto, null)
                        }
                        FilledIconToggleButton(
                            checked = !useSystemIsDark && !isDark,
                            onCheckedChange = {
                                useSystemIsDark = !it
                                settings["useSystemIsDark"] = !it
                                isDark = !it
                                settings["isDark"] = !it
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            },
                            shapes = IconButtonDefaults.toggleableShapes()
                        ) {
                            Icon(Icons.Outlined.LightMode, null)
                        }
                        FilledIconToggleButton(
                            checked = !useSystemIsDark && isDark,
                            onCheckedChange = {
                                useSystemIsDark = !it
                                settings["useSystemIsDark"] = !it
                                isDark = it
                                settings["isDark"] = it
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                            },
                            shapes = IconButtonDefaults.toggleableShapes()
                        ) {
                            Icon(Icons.Outlined.DarkMode, null)
                        }
                    }
                }
            }
            if (supportsCustomColorScheme) {
                settingsToggleItem(
                    checked = useCustomColorScheme,
                    onCheckedChange = {
                        useCustomColorScheme = it
                        settings["useCustomColorScheme"] = it
                    },
                    text = "Material-You",
                    icon = Icons.Outlined.InvertColors,
                    position = PreferencePosition.Middle,
                )
            }
            settingsToggleItem(
                checked = animationsEnabled,
                onCheckedChange = {
                    animationsEnabled = it
                    settings["animationsEnabled"] = it
                },
                text = "Animationen",
                icon = Icons.Outlined.Animation,
                position = PreferencePosition.Middle,
            )
            settingsToggleItem(
                checked = blurEnabled,
                onCheckedChange = {
                    blurEnabled = it
                    settings["blurEnabled"] = it
                },
                text = "Unschärfe-Effekt",
                icon = Icons.Outlined.BlurOn,
                position = PreferencePosition.Middle,
            )
            settingsToggleItem(
                checked = backgroundEnabled,
                onCheckedChange = {
                    backgroundEnabled = it
                    settings["backgroundEnabled"] = it
                },
                text = "Hintergrundbild",
                icon = Icons.Outlined.Texture,
                position = PreferencePosition.Bottom,
            )
            item {
                PreferenceCategory("Startseite", Modifier.padding(horizontal = 15.dp))
            }
            settingsToggleItem(
                checked = showGreetings,
                onCheckedChange = {
                    showGreetings = it
                    settings["showGreetings"] = it
                },
                text = "Begrüßung anzeigen",
                icon = Icons.Outlined.WavingHand,
                position = PreferencePosition.Top,
            )
            settingsToggleItem(
                checked = showNewestGrades,
                onCheckedChange = {
                    showNewestGrades = it
                    settings["showNewestGrades"] = it
                },
                text = "Neuste Noten anzeigen",
                icon = Icons.Outlined.FiberNew,
                position = PreferencePosition.Bottom,
            )
            item {
                PreferenceCategory("Noten", Modifier.padding(horizontal = 15.dp))
            }
            settingsToggleItem(
                checked = showGradeHistory,
                onCheckedChange = {
                    showGradeHistory = it
                    settings["showGradeHistory"] = it
                },
                text = "Noten-Historien anzeigen",
                icon = Icons.Outlined.History,
                position = PreferencePosition.Top,
            )
            settingsToggleItem(
                checked = showCollectionsWithoutGrades,
                onCheckedChange = {
                    showCollectionsWithoutGrades = it
                    settings["showCollectionsWithoutGrades"] = it
                },
                text = "Leistungen ohne Noten anzeigen",
                icon = Icons.Outlined.DisabledVisible,
                position = PreferencePosition.Bottom,
            )
            item {
                PreferenceCategory("Allgemein", Modifier.padding(horizontal = 15.dp))
            }
            settingsToggleItem(
                checked = showTeachersWithFirstname,
                onCheckedChange = {
                    showTeachersWithFirstname = it
                    settings["showTeachersWithFirstname"] = it
                },
                text = "Lehrer mit Vornamen anzeigen",
                icon = Icons.Outlined.Title,
            )
            if (biometryAuthenticator.isBiometricAvailable()) {
                item {
                    PreferenceCategory("Sicherheit", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = requireBiometricAuthentification,
                    onCheckedChange = {
                        if (it) {
                            biometryAuthenticator.checkBiometryAuthentication(
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
                    text = "Biometrische Authentifizierung erforderlich",
                    icon = Icons.Outlined.Lock,
                )
            }
            item {
                PreferenceCategory("Account", Modifier.padding(horizontal = 15.dp))
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Angemeldet als ${viewModel.user.value?.username ?: "Unbekannt"}",
                    subtitle = remember { if (viewModel.authTokenManager.getToken().isNullOrEmpty()) "(Temporär angemeldet)" else "(Anmeldung gespeichert)" },
                    icon = Icons.Outlined.AccountCircle,
                    position = PreferencePosition.Top,
                )
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Abmelden",
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    onClick = {
                        viewModel.logout()
                        onNavigateToLogin()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                    },
                    position = PreferencePosition.Bottom,
                )
            }
            item {
                PreferenceCategory("Über", Modifier.padding(horizontal = 15.dp))
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Beste-Noten-App",
                    subtitle = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    icon = Icons.Outlined.Info,
                )
            }
            item {
                Spacer(Modifier.height(12.dp))
            }
        }
        topAppBarBackground(innerPadding.calculateTopPadding())
    }
}