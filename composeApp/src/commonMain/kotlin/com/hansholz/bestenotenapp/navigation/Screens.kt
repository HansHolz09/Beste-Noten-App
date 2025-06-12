package com.hansholz.bestenotenapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val label: String, val route: String) {
    @Serializable
    data object Home : Screen("Startseite", "home")

    @Serializable
    data object Grades : Screen("Noten", "grades")

    @Serializable
    data object SubjectsAndTeachers : Screen("Fächer und Lehrer", "subjects_and_teachers")

    @Serializable
    data object Stats : Screen("Statistiken", "stats")

    @Serializable
    data object Settings : Screen("Einstellungen", "settings")

    companion object {
        val entries: List<Screen> by lazy {
            listOfNotNull(
                Home,
                Grades,
                SubjectsAndTeachers,
                Stats
            )
        }
    }
}