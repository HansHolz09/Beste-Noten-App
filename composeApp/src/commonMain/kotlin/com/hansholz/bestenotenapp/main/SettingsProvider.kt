package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.*
import com.russhwolf.settings.Settings

internal val LocalBackgroundEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowGradeHistory = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowCollectionsWithoutGrades = compositionLocalOf { mutableStateOf(false) }
internal val LocalShowTeachersWithFirstname = compositionLocalOf { mutableStateOf(false) }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val settings = Settings()

    val backgroundEnabledState = remember { mutableStateOf(settings.getBoolean("backgroundEnabled", true)) }
    val showGradeHistoryState = remember { mutableStateOf(settings.getBoolean("showGradeHistory", false)) }
    val showCollectionsWithoutGradesState = remember { mutableStateOf(settings.getBoolean("showCollectionsWithoutGrades", false)) }
    val showTeachersWithFirstnameState = remember { mutableStateOf(settings.getBoolean("showTeachersWithFirstname", false)) }
    CompositionLocalProvider(
        LocalBackgroundEnabled provides backgroundEnabledState,
        LocalShowGradeHistory provides showGradeHistoryState,
        LocalShowCollectionsWithoutGrades provides showCollectionsWithoutGradesState,
        LocalShowTeachersWithFirstname provides showTeachersWithFirstnameState
    ) {
        content()
    }
}