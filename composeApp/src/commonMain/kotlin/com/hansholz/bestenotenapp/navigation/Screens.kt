package com.hansholz.bestenotenapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Date_range
import com.composables.icons.materialsymbols.rounded.Demography
import com.composables.icons.materialsymbols.rounded.Home
import com.composables.icons.materialsymbols.rounded.School
import com.composables.icons.materialsymbols.rounded.Settings
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val route: String,
) {
    @Serializable
    data object Biometry : Screen("biometry")

    @Serializable
    data object Login : Screen("login")

    @Serializable
    data object Main : Screen("main")

    @Serializable
    data object Grades : Screen("grades")
}

@Serializable
sealed class Fragment(
    val label: String,
    @Contextual val icon: ImageVector,
    val route: String,
) {
    @Serializable
    data object Home : Fragment("Startseite", MaterialSymbols.Rounded.Home, "home")

    @Serializable
    data object Grades : Fragment("Noten", MaterialSymbols.Rounded.School, "grades")

    @Serializable
    data object Timetable : Fragment("Stundenplan", MaterialSymbols.Rounded.Date_range, "timetable")

    @Serializable
    data object SubjectsAndTeachers : Fragment("Fächer und Lehrer", MaterialSymbols.Rounded.Demography, "subjects_and_teachers")

    @Serializable
    data object Settings : Fragment("Einstellungen", MaterialSymbols.Rounded.Settings, "settings")

    companion object {
        val entries: List<Fragment> by lazy {
            listOfNotNull(
                Home,
                Grades,
                Timetable,
                SubjectsAndTeachers,
            )
        }
    }
}
