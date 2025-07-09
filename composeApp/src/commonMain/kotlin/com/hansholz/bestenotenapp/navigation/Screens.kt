package com.hansholz.bestenotenapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    data object Login : Screen("login")

    @Serializable
    data object Main : Screen("main")
}

@Serializable
sealed class Fragment(val label: String, val route: String) {

    @Serializable
    data object Home : Fragment("Startseite", "home")

    @Serializable
    data object Grades : Fragment("Noten", "grades")

    @Serializable
    data object SubjectsAndTeachers : Fragment("Fächer und Lehrer", "subjects_and_teachers")

    @Serializable
    data object Stats : Fragment("Statistiken", "stats")

    @Serializable
    data object Settings : Fragment("Einstellungen", "settings")

    companion object {
        val entries: List<Fragment> by lazy {
            listOfNotNull(
                Home,
                Grades,
                SubjectsAndTeachers,
                Stats
            )
        }
    }
}