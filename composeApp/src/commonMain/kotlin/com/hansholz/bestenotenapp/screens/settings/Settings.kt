@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens.settings

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
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Subject
import androidx.compose.material.icons.outlined.Texture
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.components.PreferenceCategory
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.icons.Github
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalGradeNotificationIntervalMinutes
import com.hansholz.bestenotenapp.main.LocalGradeNotificationsEnabled
import com.hansholz.bestenotenapp.main.LocalGradeNotificationsWifiOnly
import com.hansholz.bestenotenapp.main.LocalHapticsEnabled
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.LocalShowAbsences
import com.hansholz.bestenotenapp.main.LocalShowAllSubjects
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowCurrentLesson
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.LocalShowYearProgress
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.security.BindBiometryAuthenticatorEffect
import com.hansholz.bestenotenapp.security.BiometryAuthenticator
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalIsDark
import com.hansholz.bestenotenapp.theme.LocalSupportsCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseSystemIsDark
import com.hansholz.bestenotenapp.utils.formateInterval
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Settings(
    viewModel: ViewModel,
    onNavigateToLogin: () -> Unit,
) {
    val settingsViewModel = viewModel { SettingsViewModel() }

    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val uriHandler = LocalUriHandler.current
    val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    var useSystemIsDark by LocalUseSystemIsDark.current
    var isDark by LocalIsDark.current
    var useCustomColorScheme by LocalUseCustomColorScheme.current
    val supportsCustomColorScheme by LocalSupportsCustomColorScheme.current
    var animationsEnabled by LocalAnimationsEnabled.current
    var blurEnabled by LocalBlurEnabled.current
    var backgroundEnabled by LocalBackgroundEnabled.current
    var hapticsEnabled by LocalHapticsEnabled.current
    var notificationsEnabled by LocalGradeNotificationsEnabled.current
    var notificationIntervalMinutes by LocalGradeNotificationIntervalMinutes.current
    var notificationsWifiOnly by LocalGradeNotificationsWifiOnly.current
    var showGreetings by LocalShowGreetings.current
    var showNewestGrades by LocalShowNewestGrades.current
    var showCurrentLesson by LocalShowCurrentLesson.current
    var showYearProgress by LocalShowYearProgress.current
    var showGradeHistory by LocalShowGradeHistory.current
    var showAllSubjects by LocalShowAllSubjects.current
    var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
    var showAbsences by LocalShowAbsences.current
    var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
    var requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val settings = Settings()

    val biometryAuthenticator = remember { BiometryAuthenticator() }
    BindBiometryAuthenticatorEffect(biometryAuthenticator)

    TopAppBarScaffold(
        title = "Einstellungen",
        navigationIcon = {
            EnhancedIconButton(
                onClick = {
                    scope.launch {
                        viewModel.closeOrOpenDrawer(windowWithSizeClass)
                    }
                },
            ) {
                Icon(Icons.Filled.Menu, null)
            }
        },
        sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
        hazeState = viewModel.hazeBackgroundState,
    ) { innerPadding, topAppBarBackground ->
        LazyColumn(
            modifier = Modifier.hazeSource(viewModel.hazeBackgroundState),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                PreferenceCategory("Personalisierung", Modifier.padding(horizontal = 15.dp))
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
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
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
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
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
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
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
            if (HazeDefaults.blurEnabled()) {
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
            }
            settingsToggleItem(
                checked = backgroundEnabled,
                onCheckedChange = {
                    backgroundEnabled = it
                    settings["backgroundEnabled"] = it
                },
                text = "Hintergrundbild",
                icon = Icons.Outlined.Texture,
                position = if (vibrator.isVibrationSupported) PreferencePosition.Middle else PreferencePosition.Bottom,
            )
            if (vibrator.isVibrationSupported) {
                settingsToggleItem(
                    checked = hapticsEnabled,
                    onCheckedChange = {
                        hapticsEnabled = it
                        settings["hapticsEnabled"] = it
                    },
                    text = "Haptisches Feedback",
                    icon = Icons.Outlined.Vibration,
                    position = PreferencePosition.Bottom,
                )
            }
            if (GradeNotifications.isSupported && !viewModel.isDemoAccount.value && !viewModel.authTokenManager.getToken().isNullOrEmpty()) {
                item {
                    PreferenceCategory("Benachrichtigungen", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        scope.launch {
                            if (it) {
                                val granted = GradeNotifications.requestPermission()
                                notificationsEnabled = granted
                                settings["gradeNotificationsEnabled"] = granted
                            } else {
                                notificationsEnabled = false
                                settings["gradeNotificationsEnabled"] = false
                            }
                            GradeNotifications.onSettingsUpdated()
                        }
                    },
                    text = "Benachrichtigungen über neue Noten",
                    icon = Icons.Outlined.Notifications,
                    position = PreferencePosition.Top,
                )
                settingsToggleItem(
                    checked = notificationsWifiOnly,
                    onCheckedChange = { enabled ->
                        if (!notificationsEnabled) {
                            return@settingsToggleItem
                        }
                        notificationsWifiOnly = enabled
                        settings["gradeNotificationsWifiOnly"] = enabled
                        GradeNotifications.onSettingsUpdated()
                    },
                    text = "Nur mit WLAN überprüfen",
                    icon = Icons.Outlined.Wifi,
                    enabled = notificationsEnabled,
                    position = PreferencePosition.Middle,
                )
                item {
                    PreferenceItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = "Überprüfungsintervall",
                        subtitle = "Aktuell: ${formateInterval(notificationIntervalMinutes)}",
                        icon = Icons.Outlined.History,
                        enabled = notificationsEnabled,
                        onClick =
                            if (notificationsEnabled) {
                                {
                                    vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                    settingsViewModel.showIntervalDialog = true
                                }
                            } else {
                                null
                            },
                        position = PreferencePosition.Bottom,
                    )
                }
            }
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

                    scope.launch {
                        if (it && viewModel.startGradeCollections.isEmpty()) {
                            viewModel.getCollections()?.let { viewModel.startGradeCollections.addAll(it) }
                        }
                    }
                },
                text = "Neuste Noten anzeigen",
                icon = Icons.Outlined.FiberNew,
                position = PreferencePosition.Middle,
            )
            settingsToggleItem(
                checked = showCurrentLesson,
                onCheckedChange = {
                    showCurrentLesson = it
                    settings["showCurrentLesson"] = it

                    scope.launch {
                        if (it && viewModel.currentJournalDay.value == null) {
                            @OptIn(ExperimentalTime::class)
                            val currentDate =
                                Clock.System
                                    .now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                    .let {
                                        "${it.year}-${it.month.number.toString().padStart(2, '0')}" +
                                            "-${it.day.toString().padStart(2, '0')}"
                                    }
                            viewModel.currentJournalDay.value = viewModel.getJournalWeek()?.days?.find { it.date == currentDate }
                        }
                    }
                },
                text = "Aktuellen Schultag anzeigen",
                icon = Icons.Outlined.FormatListBulleted,
                position = if (viewModel.isDemoAccount.value) PreferencePosition.Bottom else PreferencePosition.Middle,
            )
            if (!viewModel.isDemoAccount.value) {
                settingsToggleItem(
                    checked = showYearProgress,
                    onCheckedChange = {
                        showYearProgress = it
                        settings["showYearProgress"] = it

                        scope.launch {
                            if (it && viewModel.intervals.isEmpty()) {
                                viewModel.getIntervals()?.let { viewModel.intervals.addAll(it) }
                            }
                        }
                    },
                    text = "Schuljahres-Fortschritt anzeigen",
                    icon = Icons.Outlined.Percent,
                    position = PreferencePosition.Bottom,
                )
            }
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
                position = if (viewModel.isDemoAccount.value) PreferencePosition.Single else PreferencePosition.Top,
            )
            if (!viewModel.isDemoAccount.value) {
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
                    PreferenceCategory("Stundenplan", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showAbsences,
                    onCheckedChange = {
                        showAbsences = it
                        settings["showAbsences"] = it

                        scope.launch {
                            if (it && viewModel.absences.isEmpty()) {
                                val currentYearId =
                                    viewModel.years
                                        .last()
                                        .id
                                        .toString()
                                viewModel.getAbsences(currentYearId)?.let { viewModel.absences.add(currentYearId to it) }
                            }
                        }
                    },
                    text = "Abwesenheits-Einträge anzeigen",
                    icon = Icons.Outlined.HowToReg,
                )
                item {
                    PreferenceCategory("Fächer und Lehrer", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showAllSubjects,
                    onCheckedChange = {
                        showAllSubjects = it
                        settings["showAllSubjects"] = it
                    },
                    text = "Alle Fächer der Schule anzeigen",
                    icon = Icons.Outlined.Subject,
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
            }
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
                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
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
                    title = "Quellcode auf GitHub",
                    subtitle = "HansHolz09/Beste-Noten-App",
                    icon = Github,
                    onClick = {
                        uriHandler.openUri("https://github.com/HansHolz09/Beste-Noten-App")
                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                    },
                    position = PreferencePosition.Top,
                )
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Open-Source-Lizenzen",
                    icon = Icons.Outlined.LocalLibrary,
                    onClick = {
                        settingsViewModel.showLicenseDialog = true
                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                    },
                    position = PreferencePosition.Middle,
                )
            }
            item {
                var tapCount by remember { mutableStateOf(0) }
                val func = {
                    tapCount++
                    if (tapCount % 7 == 0) {
                        settingsViewModel.showConfetti = true
                    } else {
                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                    }
                }
                val onClick: (() -> Unit)? = if (!settingsViewModel.showConfetti) func else null
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Beste-Noten-App",
                    subtitle = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    icon = Icons.Outlined.Info,
                    onClick = onClick,
                    position = PreferencePosition.Bottom,
                )
            }
            item {
                Spacer(Modifier.height(12.dp))
            }
        }
        topAppBarBackground(innerPadding.calculateTopPadding())
    }

    NotificationIntervalDialog(settingsViewModel)
    LibrariesDialog(settingsViewModel)
    ConfettiEasterEgg(settingsViewModel)
}
