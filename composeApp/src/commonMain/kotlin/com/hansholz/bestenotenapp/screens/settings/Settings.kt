@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Account_circle
import com.composables.icons.materialsymbols.rounded.Animation
import com.composables.icons.materialsymbols.rounded.Article
import com.composables.icons.materialsymbols.rounded.Balance
import com.composables.icons.materialsymbols.rounded.Blur_on
import com.composables.icons.materialsymbols.rounded.Brightness_4
import com.composables.icons.materialsymbols.rounded.Brightness_auto
import com.composables.icons.materialsymbols.rounded.Dark_mode
import com.composables.icons.materialsymbols.rounded.Disabled_visible
import com.composables.icons.materialsymbols.rounded.Fiber_new
import com.composables.icons.materialsymbols.rounded.File_export
import com.composables.icons.materialsymbols.rounded.Format_list_bulleted
import com.composables.icons.materialsymbols.rounded.History
import com.composables.icons.materialsymbols.rounded.How_to_reg
import com.composables.icons.materialsymbols.rounded.Info
import com.composables.icons.materialsymbols.rounded.Invert_colors
import com.composables.icons.materialsymbols.rounded.Light_mode
import com.composables.icons.materialsymbols.rounded.Local_library
import com.composables.icons.materialsymbols.rounded.Lock
import com.composables.icons.materialsymbols.rounded.Logout
import com.composables.icons.materialsymbols.rounded.Menu
import com.composables.icons.materialsymbols.rounded.Notifications
import com.composables.icons.materialsymbols.rounded.Percent
import com.composables.icons.materialsymbols.rounded.Settings_backup_restore
import com.composables.icons.materialsymbols.rounded.Subject
import com.composables.icons.materialsymbols.rounded.Texture
import com.composables.icons.materialsymbols.rounded.Title
import com.composables.icons.materialsymbols.rounded.Vibration
import com.composables.icons.materialsymbols.rounded.Waving_hand
import com.composables.icons.materialsymbols.rounded.Wifi
import com.hansholz.bestenotenapp.components.PreferenceCategory
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.icons.Github
import com.hansholz.bestenotenapp.components.icons.MathAvg
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalGradeAverageEnabled
import com.hansholz.bestenotenapp.main.LocalGradeAverageUseWeighting
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
import com.hansholz.bestenotenapp.main.LocalShowNotes
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.LocalShowYearProgress
import com.hansholz.bestenotenapp.main.Platform
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.main.getPlatform
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.screens.grades.GradeAverageCalculator
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalIsDark
import com.hansholz.bestenotenapp.theme.LocalSupportsCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseCustomColorScheme
import com.hansholz.bestenotenapp.theme.LocalUseSystemIsDark
import com.hansholz.bestenotenapp.utils.formateInterval
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.hazeSource
import eu.anifantakis.lib.ksafe.compose.mutableStateOf
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
    var gradeAverageEnabled by LocalGradeAverageEnabled.current
    var gradeAverageUseWeighting by LocalGradeAverageUseWeighting.current
    var showAllSubjects by LocalShowAllSubjects.current
    var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
    var showAbsences by LocalShowAbsences.current
    var showNotes by LocalShowNotes.current
    var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
    var requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val kSafe = remember { kSafe() }
    val authToken by kSafe.mutableStateOf("", "authToken")

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
                Icon(MaterialSymbols.Rounded.Menu, null)
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
                    icon = MaterialSymbols.Rounded.Brightness_4,
                    position = PreferencePosition.Top,
                ) {
                    Row {
                        FilledIconToggleButton(
                            checked = useSystemIsDark,
                            onCheckedChange = {
                                useSystemIsDark = it
                                kSafe.putDirect("useSystemIsDark", it)
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
                        ) {
                            Icon(MaterialSymbols.Rounded.Brightness_auto, null)
                        }
                        FilledIconToggleButton(
                            checked = !useSystemIsDark && !isDark,
                            onCheckedChange = {
                                useSystemIsDark = !it
                                kSafe.putDirect("useSystemIsDark", !it)
                                isDark = !it
                                kSafe.putDirect("isDark", !it)
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
                        ) {
                            Icon(MaterialSymbols.Rounded.Light_mode, null)
                        }
                        FilledIconToggleButton(
                            checked = !useSystemIsDark && isDark,
                            onCheckedChange = {
                                useSystemIsDark = !it
                                kSafe.putDirect("useSystemIsDark", !it)
                                isDark = it
                                kSafe.putDirect("isDark", it)
                                vibrator.enhancedVibrate(EnhancedVibrations.SLOW_RISE)
                            },
                            shapes = IconButtonDefaults.toggleableShapes(),
                        ) {
                            Icon(MaterialSymbols.Rounded.Dark_mode, null)
                        }
                    }
                }
            }
            if (supportsCustomColorScheme) {
                settingsToggleItem(
                    checked = useCustomColorScheme,
                    onCheckedChange = {
                        useCustomColorScheme = it
                        kSafe.putDirect("useCustomColorScheme", it)
                    },
                    text = "Material-You",
                    icon = MaterialSymbols.Rounded.Invert_colors,
                    position = PreferencePosition.Middle,
                )
            }
            settingsToggleItem(
                checked = animationsEnabled,
                onCheckedChange = {
                    animationsEnabled = it
                    kSafe.putDirect("animationsEnabled", it)
                },
                text = "Animationen",
                icon = MaterialSymbols.Rounded.Animation,
                position = PreferencePosition.Middle,
            )
            if (HazeDefaults.blurEnabled()) {
                settingsToggleItem(
                    checked = blurEnabled,
                    onCheckedChange = {
                        blurEnabled = it
                        kSafe.putDirect("blurEnabled", it)
                    },
                    text = "Unschärfe-Effekt",
                    icon = MaterialSymbols.Rounded.Blur_on,
                    position = PreferencePosition.Middle,
                )
            }
            settingsToggleItem(
                checked = backgroundEnabled,
                onCheckedChange = {
                    backgroundEnabled = it
                    kSafe.putDirect("backgroundEnabled", it)
                },
                text = "Hintergrundbild",
                icon = MaterialSymbols.Rounded.Texture,
                position = if (vibrator.isVibrationSupported) PreferencePosition.Middle else PreferencePosition.Bottom,
            )
            if (vibrator.isVibrationSupported) {
                settingsToggleItem(
                    checked = hapticsEnabled,
                    onCheckedChange = {
                        hapticsEnabled = it
                        kSafe.putDirect("hapticsEnabled", it, false)
                        if (it) vibrator.enhancedVibrate(EnhancedVibrations.TOGGLE_ON, true)
                    },
                    text = "Haptisches Feedback",
                    icon = MaterialSymbols.Rounded.Vibration,
                    position = PreferencePosition.Bottom,
                    hapticsEnabled = false,
                )
            }
            if (GradeNotifications.isSupported && !viewModel.isDemoAccount.value && authToken.isNotEmpty()) {
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
                                kSafe.putDirect("gradeNotificationsEnabled", it)
                            } else {
                                notificationsEnabled = false
                                kSafe.putDirect("gradeNotificationsEnabled", it)
                            }
                            GradeNotifications.onSettingsUpdated()
                        }
                    },
                    text = "Benachrichtigungen über neue Noten",
                    icon = MaterialSymbols.Rounded.Notifications,
                    position = PreferencePosition.Top,
                )
                settingsToggleItem(
                    checked = notificationsWifiOnly,
                    onCheckedChange = { enabled ->
                        if (!notificationsEnabled) {
                            return@settingsToggleItem
                        }
                        notificationsWifiOnly = enabled
                        kSafe.putDirect("gradeNotificationsWifiOnly", enabled)
                        GradeNotifications.onSettingsUpdated()
                    },
                    text = "Nur mit WLAN überprüfen",
                    icon = MaterialSymbols.Rounded.Wifi,
                    enabled = notificationsEnabled,
                    position = PreferencePosition.Middle,
                )
                item {
                    PreferenceItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = "Überprüfungsintervall",
                        subtitle = "Aktuell: ${formateInterval(notificationIntervalMinutes)}",
                        icon = MaterialSymbols.Rounded.History,
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
                    kSafe.putDirect("showGreetings", it)
                },
                text = "Begrüßung anzeigen",
                icon = MaterialSymbols.Rounded.Waving_hand,
                position = PreferencePosition.Top,
            )
            settingsToggleItem(
                checked = showNewestGrades,
                onCheckedChange = {
                    showNewestGrades = it
                    kSafe.putDirect("showNewestGrades", it)

                    scope.launch {
                        if (it && viewModel.startGradeCollections.isEmpty()) {
                            viewModel.getCollections()?.let { viewModel.startGradeCollections.addAll(it) }
                        }
                    }
                },
                text = "Neuste Noten anzeigen",
                icon = MaterialSymbols.Rounded.Fiber_new,
                position = PreferencePosition.Middle,
            )
            settingsToggleItem(
                checked = showCurrentLesson,
                onCheckedChange = {
                    showCurrentLesson = it
                    kSafe.putDirect("showCurrentLesson", it)

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
                icon = MaterialSymbols.Rounded.Format_list_bulleted,
                position = if (viewModel.isDemoAccount.value) PreferencePosition.Bottom else PreferencePosition.Middle,
            )
            if (!viewModel.isDemoAccount.value) {
                settingsToggleItem(
                    checked = showYearProgress,
                    onCheckedChange = {
                        showYearProgress = it
                        kSafe.putDirect("showYearProgress", it)

                        scope.launch {
                            if (it && viewModel.intervals.isEmpty()) {
                                viewModel.getIntervals()?.let { viewModel.intervals.addAll(it) }
                            }
                        }
                    },
                    text = "Schuljahres-Fortschritt anzeigen",
                    icon = MaterialSymbols.Rounded.Percent,
                    position = PreferencePosition.Bottom,
                )
            }
            item {
                PreferenceCategory("Noten", Modifier.padding(horizontal = 15.dp))
            }
            settingsToggleItem(
                checked = gradeAverageEnabled,
                onCheckedChange = {
                    gradeAverageEnabled = it
                    kSafe.putDirect("gradeAverageEnabled", it)
                },
                text = "Durchschnittsberechnung aktiv",
                icon = MathAvg,
                position = PreferencePosition.Top,
            )
            settingsToggleItem(
                checked = gradeAverageUseWeighting,
                onCheckedChange = {
                    if (gradeAverageUseWeighting != it) {
                        gradeAverageUseWeighting = it
                        kSafe.putDirect("gradeAverageUseWeighting", it)
                        GradeAverageCalculator().convertStoredSubjectWeightingsMode(
                            kSafe = kSafe,
                            useWeightingInsteadOfPercent = it,
                        )
                    }
                },
                text = "Mit Gewichtungen statt Prozenten rechnen",
                icon = MaterialSymbols.Rounded.Balance,
                position = PreferencePosition.Middle,
            )
            settingsToggleItem(
                checked = showGradeHistory,
                onCheckedChange = {
                    showGradeHistory = it
                    kSafe.putDirect("showGradeHistory", it)
                },
                text = "Noten-Historien anzeigen",
                icon = MaterialSymbols.Rounded.History,
                position = if (viewModel.isDemoAccount.value) PreferencePosition.Bottom else PreferencePosition.Middle,
            )
            if (!viewModel.isDemoAccount.value) {
                settingsToggleItem(
                    checked = showCollectionsWithoutGrades,
                    onCheckedChange = {
                        showCollectionsWithoutGrades = it
                        kSafe.putDirect("showCollectionsWithoutGrades", it)
                    },
                    text = "Leistungen ohne Noten anzeigen",
                    icon = MaterialSymbols.Rounded.Disabled_visible,
                    position = PreferencePosition.Bottom,
                )
                item {
                    PreferenceCategory("Stundenplan", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showAbsences,
                    onCheckedChange = {
                        showAbsences = it
                        kSafe.putDirect("showAbsences", it)

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
                    icon = MaterialSymbols.Rounded.How_to_reg,
                    position = PreferencePosition.Top,
                )
                settingsToggleItem(
                    checked = showNotes,
                    onCheckedChange = {
                        showNotes = it
                        kSafe.putDirect("showNotes", it)
                    },
                    text = "Tages-Notizen anzeigen",
                    icon = MaterialSymbols.Rounded.Article,
                    position = PreferencePosition.Bottom,
                )
                item {
                    PreferenceCategory("Fächer und Lehrer", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showAllSubjects,
                    onCheckedChange = {
                        showAllSubjects = it
                        kSafe.putDirect("showAllSubjects", it)
                    },
                    text = "Alle Fächer der Schule anzeigen",
                    icon = MaterialSymbols.Rounded.Subject,
                )
                item {
                    PreferenceCategory("Allgemein", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showTeachersWithFirstname,
                    onCheckedChange = {
                        showTeachersWithFirstname = it
                        kSafe.putDirect("showTeachersWithFirstname", it)
                    },
                    text = "Lehrer mit Vornamen anzeigen",
                    icon = MaterialSymbols.Rounded.Title,
                )
            }
            if (listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform())) {
                item {
                    PreferenceCategory("Sicherheit", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = requireBiometricAuthentification,
                    onCheckedChange = {
                        if (it) {
                            kSafe.verifyBiometricDirect("Bestätige, um die biometrische Authentifizierung beim Start zu aktiven.") { isSuccessful ->
                                if (isSuccessful) {
                                    requireBiometricAuthentification = it
                                    kSafe.putDirect("requireBiometricAuthentification", it)
                                }
                            }
                        } else {
                            requireBiometricAuthentification = it
                            kSafe.putDirect("requireBiometricAuthentification", it)
                        }
                    },
                    text = "Biometrische Authentifizierung erforderlich",
                    icon = MaterialSymbols.Rounded.Lock,
                )
            }
            item {
                PreferenceCategory("Datensicherung", Modifier.padding(horizontal = 15.dp))
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Daten als JSON exportieren",
                    icon = MaterialSymbols.Rounded.File_export,
                    onClick = {
                        settingsViewModel.showExportConfigDialog = true
                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                    },
                    position = PreferencePosition.Top,
                )
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Daten aus JSON wiederherstellen",
                    icon = MaterialSymbols.Rounded.Settings_backup_restore,
                    onClick = {
                        scope.launch {
                            settingsViewModel.importJson(viewModel) { appSettings ->
                                useSystemIsDark = appSettings.useSystemIsDark
                                isDark = appSettings.isDark
                                useCustomColorScheme = appSettings.useCustomColorScheme
                                animationsEnabled = appSettings.animationsEnabled
                                blurEnabled = appSettings.blurEnabled && HazeDefaults.blurEnabled()
                                backgroundEnabled = appSettings.backgroundEnabled
                                hapticsEnabled = appSettings.hapticsEnabled
                                showGreetings = appSettings.showGreetings
                                showNewestGrades = appSettings.showNewestGrades
                                showCurrentLesson = appSettings.showCurrentLesson
                                showYearProgress = appSettings.showYearProgress
                                showGradeHistory = appSettings.showGradeHistory
                                gradeAverageEnabled = appSettings.gradeAverageEnabled
                                gradeAverageUseWeighting = appSettings.gradeAverageUseWeighting
                                showAllSubjects = appSettings.showAllSubjects
                                showCollectionsWithoutGrades = appSettings.showCollectionsWithoutGrades
                                showAbsences = appSettings.showAbsences
                                showNotes = appSettings.showNotes
                                showTeachersWithFirstname = appSettings.showTeachersWithFirstname
                                notificationsEnabled = appSettings.gradeNotificationsEnabled
                                notificationIntervalMinutes = appSettings.gradeNotificationsIntervalMinutes
                                notificationsWifiOnly = appSettings.gradeNotificationsWifiOnly
                                requireBiometricAuthentification = appSettings.requireBiometricAuthentification
                                kSafe.putDirect("useSystemIsDark", appSettings.useSystemIsDark)
                                kSafe.putDirect("isDark", appSettings.isDark)
                                kSafe.putDirect("useCustomColorScheme", appSettings.useCustomColorScheme)
                                kSafe.putDirect("animationsEnabled", appSettings.animationsEnabled)
                                kSafe.putDirect("blurEnabled", appSettings.blurEnabled && HazeDefaults.blurEnabled())
                                kSafe.putDirect("backgroundEnabled", appSettings.backgroundEnabled)
                                kSafe.putDirect("hapticsEnabled", appSettings.hapticsEnabled, false)
                                kSafe.putDirect("showGreetings", appSettings.showGreetings)
                                kSafe.putDirect("showNewestGrades", appSettings.showNewestGrades)
                                kSafe.putDirect("showCurrentLesson", appSettings.showCurrentLesson)
                                kSafe.putDirect("showYearProgress", appSettings.showYearProgress)
                                kSafe.putDirect("showGradeHistory", appSettings.showGradeHistory)
                                kSafe.putDirect("gradeAverageEnabled", appSettings.gradeAverageEnabled)
                                kSafe.putDirect("gradeAverageUseWeighting", appSettings.gradeAverageUseWeighting)
                                kSafe.putDirect("showAllSubjects", appSettings.showAllSubjects)
                                kSafe.putDirect("showCollectionsWithoutGrades", appSettings.showCollectionsWithoutGrades)
                                kSafe.putDirect("showAbsences", appSettings.showAbsences)
                                kSafe.putDirect("showNotes", appSettings.showNotes)
                                kSafe.putDirect("showTeachersWithFirstname", appSettings.showTeachersWithFirstname)
                                kSafe.putDirect("gradeNotificationsEnabled", appSettings.gradeNotificationsEnabled)
                                kSafe.putDirect("gradeNotificationsIntervalMinutes", appSettings.gradeNotificationsIntervalMinutes)
                                kSafe.putDirect("gradeNotificationsWifiOnly", appSettings.gradeNotificationsWifiOnly)
                                kSafe.putDirect("requireBiometricAuthentification", appSettings.requireBiometricAuthentification)
                                GradeNotifications.onSettingsUpdated()
                            }
                        }
                    },
                    position = PreferencePosition.Bottom,
                )
            }
            item {
                PreferenceCategory("Account", Modifier.padding(horizontal = 15.dp))
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Angemeldet als ${viewModel.user.value?.username ?: "Unbekannt"}",
                    subtitle = remember { if (authToken.isEmpty()) "(Temporär angemeldet)" else "(Anmeldung gespeichert)" },
                    icon = MaterialSymbols.Rounded.Account_circle,
                    position = PreferencePosition.Top,
                )
            }
            item {
                PreferenceItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "Abmelden",
                    icon = MaterialSymbols.Rounded.Logout,
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
                    icon = MaterialSymbols.Rounded.Local_library,
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
                    icon = MaterialSymbols.Rounded.Info,
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
    ExportConfigDialog(settingsViewModel, viewModel)
    LibrariesDialog(settingsViewModel)
    ConfettiEasterEgg(settingsViewModel)
}
