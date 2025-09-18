package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.russhwolf.settings.Settings

internal val LocalBackgroundEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGreetings = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowNewestGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCurrentLesson = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGradeHistory = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowAllSubjects = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCollectionsWithoutGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowTeachersWithFirstname = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeNotificationsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalGradeNotificationIntervalMinutes =
    compositionLocalOf { mutableStateOf(GradeNotifications.DEFAULT_INTERVAL_MINUTES) }

internal val LocalRequireBiometricAuthentification = compositionLocalOf { mutableStateOf(false) }

internal val LocalTitleBarModifier = compositionLocalOf<Modifier> { Modifier }
internal val LocalNavigationDrawerTopPadding = compositionLocalOf<Dp?> { null }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val settings = Settings()

    val backgroundEnabledState = remember { mutableStateOf(settings.getBoolean("backgroundEnabled", true)) }
    val showGreetingsState = remember { mutableStateOf(settings.getBoolean("showGreetings", true)) }
    val showNewestGradesState = remember { mutableStateOf(settings.getBoolean("showNewestGrades", true)) }
    val showCurrentLessonState = remember { mutableStateOf(settings.getBoolean("showCurrentLesson", true)) }
    val showGradeHistoryState = remember { mutableStateOf(settings.getBoolean("showGradeHistory", false)) }
    val showAllSubjectsState = remember { mutableStateOf(settings.getBoolean("showAllSubjects", false)) }
    val showCollectionsWithoutGradesState = remember { mutableStateOf(settings.getBoolean("showCollectionsWithoutGrades", false)) }
    val showTeachersWithFirstnameState = remember { mutableStateOf(settings.getBoolean("showTeachersWithFirstname", false)) }
    val requireBiometricAuthentificationState = remember { mutableStateOf(settings.getBoolean("requireBiometricAuthentification", false)) }
    val gradeNotificationsEnabledState = remember { mutableStateOf(settings.getBoolean(GradeNotifications.KEY_ENABLED, false)) }
    val gradeNotificationIntervalState = remember {
        mutableStateOf(
            settings.getLong(
                GradeNotifications.KEY_INTERVAL_MINUTES,
                GradeNotifications.DEFAULT_INTERVAL_MINUTES
            )
        )
    }
    CompositionLocalProvider(
        LocalBackgroundEnabled provides backgroundEnabledState,
        LocalShowGreetings provides showGreetingsState,
        LocalShowNewestGrades provides showNewestGradesState,
        LocalShowCurrentLesson provides showCurrentLessonState,
        LocalShowGradeHistory provides showGradeHistoryState,
        LocalShowAllSubjects provides showAllSubjectsState,
        LocalShowCollectionsWithoutGrades provides showCollectionsWithoutGradesState,
        LocalShowTeachersWithFirstname provides showTeachersWithFirstnameState,
        LocalRequireBiometricAuthentification provides requireBiometricAuthentificationState,
        LocalGradeNotificationsEnabled provides gradeNotificationsEnabledState,
        LocalGradeNotificationIntervalMinutes provides gradeNotificationIntervalState
    ) {
        content()
    }
}