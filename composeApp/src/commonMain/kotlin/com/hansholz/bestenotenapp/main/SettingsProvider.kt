package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.russhwolf.settings.Settings

internal val LocalBackgroundEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGreetings = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowNewestGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGradeHistory = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCollectionsWithoutGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowTeachersWithFirstname = compositionLocalOf { mutableStateOf(false) }

internal val LocalTitleBarModifier = compositionLocalOf<Modifier> { Modifier }
internal val LocalNavigationDrawerTopPadding = compositionLocalOf<Dp?> { null }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val settings = Settings()

    val backgroundEnabledState = remember { mutableStateOf(settings.getBoolean("backgroundEnabled", true)) }
    val showGreetingsState = remember { mutableStateOf(settings.getBoolean("showGreetings", true)) }
    val showNewestGradesState = remember { mutableStateOf(settings.getBoolean("showNewestGrades", true)) }
    val showGradeHistoryState = remember { mutableStateOf(settings.getBoolean("showGradeHistory", false)) }
    val showCollectionsWithoutGradesState = remember { mutableStateOf(settings.getBoolean("showCollectionsWithoutGrades", false)) }
    val showTeachersWithFirstnameState = remember { mutableStateOf(settings.getBoolean("showTeachersWithFirstname", false)) }
    CompositionLocalProvider(
        LocalBackgroundEnabled provides backgroundEnabledState,
        LocalShowGreetings provides showGreetingsState,
        LocalShowNewestGrades provides showNewestGradesState,
        LocalShowGradeHistory provides showGradeHistoryState,
        LocalShowCollectionsWithoutGrades provides showCollectionsWithoutGradesState,
        LocalShowTeachersWithFirstname provides showTeachersWithFirstnameState
    ) {
        content()
    }
}