package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import dev.chrisbanes.haze.HazeState

internal val LocalBackgroundEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalHapticsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGreetings = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowNewestGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCurrentLesson = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowYearProgress = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGradeHistory = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeAverageEnabled = compositionLocalOf { mutableStateOf(true) }
internal val LocalGradeAverageUseWeighting = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowAllSubjects = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCollectionsWithoutGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowAbsences = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowNotes = compositionLocalOf { mutableStateOf(false) }
internal val LocalTimetableBlockViewEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalHomeworkEnabled = compositionLocalOf { mutableStateOf(true) }
internal val LocalHomeworkGoogleSyncEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowTeachersWithFirstname = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowOnlyRelevantData = compositionLocalOf { mutableStateOf(true) }
internal val LocalGradeNotificationsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeNotificationIntervalMinutes = compositionLocalOf { mutableStateOf(60L) }
internal val LocalGradeNotificationsWifiOnly = compositionLocalOf { mutableStateOf(false) }
internal val LocalUsingOfflineCache = compositionLocalOf { mutableStateOf(false) }

internal val LocalRequireBiometricAuthentification = compositionLocalOf { mutableStateOf(false) }

internal val AppHazeState = compositionLocalOf { mutableStateOf(HazeState()) }

internal val LocalNavigationDrawerTopPadding = compositionLocalOf<Dp?> { null }

val LocalBiometricAuthenticationAvailable = compositionLocalOf { false }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) =
    kSafeProviderCompose {
        val backgroundEnabledState = remember { mutableStateOf(get("backgroundEnabled", true)) }
        val hapticsEnabledState = remember { mutableStateOf(get("hapticsEnabled", listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform()))) }
        val showGreetingsState = remember { mutableStateOf(get("showGreetings", true)) }
        val showNewestGradesState = remember { mutableStateOf(get("showNewestGrades", true)) }
        val showCurrentLessonState = remember { mutableStateOf(get("showCurrentLesson", true)) }
        val showYearProgress = remember { mutableStateOf(get("showYearProgress", true)) }
        val showGradeHistoryState = remember { mutableStateOf(get("showGradeHistory", false)) }
        val gradeAverageEnabledState = remember { mutableStateOf(get("gradeAverageEnabled", true)) }
        val gradeAverageUseWeightingState = remember { mutableStateOf(get("gradeAverageUseWeighting", false)) }
        val showAllSubjectsState = remember { mutableStateOf(get("showAllSubjects", false)) }
        val showCollectionsWithoutGradesState = remember { mutableStateOf(get("showCollectionsWithoutGrades", false)) }
        val showAbsences = remember { mutableStateOf(get("showAbsences", true)) }
        val showNotes = remember { mutableStateOf(get("showNotes", true)) }
        val timetableBlockViewEnabled = remember { mutableStateOf(get("timetableBlockViewEnabled", false)) }
        val homeworkEnabled = remember { mutableStateOf(get("homeworkEnabled", true)) }
        val homeworkGoogleSyncEnabled = remember { mutableStateOf(get("homeworkGoogleSyncEnabled", false)) }
        val showTeachersWithFirstnameState = remember { mutableStateOf(get("showTeachersWithFirstname", false)) }
        val showOnlyRelevantDataState = remember { mutableStateOf(get("showOnlyRelevantData", get("showOnlyGroupRelevantData", true))) }
        val gradeNotificationsEnabledState = remember { mutableStateOf(get("gradeNotificationsEnabled", false)) }
        val gradeNotificationIntervalState = remember { mutableStateOf(get("gradeNotificationsIntervalMinutes", 60L)) }
        val gradeNotificationsWifiOnlyState = remember { mutableStateOf(get("gradeNotificationsWifiOnly", false)) }
        val requireBiometricAuthentificationState = remember { mutableStateOf(get("requireBiometricAuthentification", false)) }
        CompositionLocalProvider(
            LocalBackgroundEnabled provides backgroundEnabledState,
            LocalHapticsEnabled provides hapticsEnabledState,
            LocalShowGreetings provides showGreetingsState,
            LocalShowNewestGrades provides showNewestGradesState,
            LocalShowCurrentLesson provides showCurrentLessonState,
            LocalShowYearProgress provides showYearProgress,
            LocalShowGradeHistory provides showGradeHistoryState,
            LocalGradeAverageEnabled provides gradeAverageEnabledState,
            LocalGradeAverageUseWeighting provides gradeAverageUseWeightingState,
            LocalShowAllSubjects provides showAllSubjectsState,
            LocalShowCollectionsWithoutGrades provides showCollectionsWithoutGradesState,
            LocalShowAbsences provides showAbsences,
            LocalShowNotes provides showNotes,
            LocalTimetableBlockViewEnabled provides timetableBlockViewEnabled,
            LocalHomeworkEnabled provides homeworkEnabled,
            LocalHomeworkGoogleSyncEnabled provides homeworkGoogleSyncEnabled,
            LocalShowTeachersWithFirstname provides showTeachersWithFirstnameState,
            LocalShowOnlyRelevantData provides showOnlyRelevantDataState,
            LocalGradeNotificationsEnabled provides gradeNotificationsEnabledState,
            LocalGradeNotificationIntervalMinutes provides gradeNotificationIntervalState,
            LocalGradeNotificationsWifiOnly provides gradeNotificationsWifiOnlyState,
            LocalRequireBiometricAuthentification provides requireBiometricAuthentificationState,
        ) {
            content()
        }
    }
