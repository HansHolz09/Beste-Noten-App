package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.hansholz.bestenotenapp.security.kSafe
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
internal val LocalShowTeachersWithFirstname = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeNotificationsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeNotificationIntervalMinutes = compositionLocalOf { mutableStateOf(60L) }
internal val LocalGradeNotificationsWifiOnly = compositionLocalOf { mutableStateOf(false) }

internal val LocalRequireBiometricAuthentification = compositionLocalOf { mutableStateOf(false) }

internal val AppHazeState = compositionLocalOf { mutableStateOf(HazeState()) }

internal val LocalTitleBarModifier = compositionLocalOf<Modifier> { Modifier }
internal val LocalNavigationDrawerTopPadding = compositionLocalOf<Dp?> { null }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val kSafe = remember { kSafe() }

    val backgroundEnabledState = remember { mutableStateOf(kSafe.getDirect("backgroundEnabled", true)) }
    val hapticsEnabledState = remember { mutableStateOf(kSafe.getDirect("hapticsEnabled", listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform()), false)) }
    val showGreetingsState = remember { mutableStateOf(kSafe.getDirect("showGreetings", true)) }
    val showNewestGradesState = remember { mutableStateOf(kSafe.getDirect("showNewestGrades", true)) }
    val showCurrentLessonState = remember { mutableStateOf(kSafe.getDirect("showCurrentLesson", true)) }
    val showYearProgress = remember { mutableStateOf(kSafe.getDirect("showYearProgress", true)) }
    val showGradeHistoryState = remember { mutableStateOf(kSafe.getDirect("showGradeHistory", false)) }
    val gradeAverageEnabledState = remember { mutableStateOf(kSafe.getDirect("gradeAverageEnabled", true)) }
    val gradeAverageUseWeightingState = remember { mutableStateOf(kSafe.getDirect("gradeAverageUseWeighting", false)) }
    val showAllSubjectsState = remember { mutableStateOf(kSafe.getDirect("showAllSubjects", false)) }
    val showCollectionsWithoutGradesState = remember { mutableStateOf(kSafe.getDirect("showCollectionsWithoutGrades", false)) }
    val showAbsences = remember { mutableStateOf(kSafe.getDirect("showAbsences", true)) }
    val showNotes = remember { mutableStateOf(kSafe.getDirect("showNotes", true)) }
    val showTeachersWithFirstnameState = remember { mutableStateOf(kSafe.getDirect("showTeachersWithFirstname", false)) }
    val gradeNotificationsEnabledState = remember { mutableStateOf(kSafe.getDirect("gradeNotificationsEnabled", false)) }
    val gradeNotificationIntervalState = remember { mutableStateOf(kSafe.getDirect("gradeNotificationsIntervalMinutes", 60L)) }
    val gradeNotificationsWifiOnlyState = remember { mutableStateOf(kSafe.getDirect("gradeNotificationsWifiOnly", false)) }
    val requireBiometricAuthentificationState = remember { mutableStateOf(kSafe.getDirect("requireBiometricAuthentification", false)) }
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
        LocalShowTeachersWithFirstname provides showTeachersWithFirstnameState,
        LocalGradeNotificationsEnabled provides gradeNotificationsEnabledState,
        LocalGradeNotificationIntervalMinutes provides gradeNotificationIntervalState,
        LocalGradeNotificationsWifiOnly provides gradeNotificationsWifiOnlyState,
        LocalRequireBiometricAuthentification provides requireBiometricAuthentificationState,
    ) {
        content()
    }
}
