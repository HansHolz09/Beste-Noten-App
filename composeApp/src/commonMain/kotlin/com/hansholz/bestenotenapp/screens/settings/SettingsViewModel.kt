package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.data.AppSettings
import com.hansholz.bestenotenapp.data.ExportData
import com.hansholz.bestenotenapp.screens.grades.GradeAverageCalculator
import com.hansholz.bestenotenapp.security.kSafeProvider
import com.hansholz.bestenotenapp.utils.IO
import dev.chrisbanes.haze.HazeDefaults
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.deprecated.openFileSaver
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readString
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class SettingsViewModel : ViewModel() {
    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    private val gradeAverageCalculator = GradeAverageCalculator()

    var showIntervalDialog by mutableStateOf(false)
    var showExportConfigDialog by mutableStateOf(false)
    var showLicenseDialog by mutableStateOf(false)
    var showConfetti by mutableStateOf(false)

    var isExporting by mutableStateOf(false)

    suspend fun exportAsJson(
        viewModel: com.hansholz.bestenotenapp.main.ViewModel,
        appSettings: Boolean,
        gradeWeights: Boolean,
        gradeYears: List<Year>?,
    ) = withContext(Dispatchers.IO) {
        try {
            kSafeProvider(viewModel.kSafe) {
                val appSettingsData =
                    if (appSettings) {
                        AppSettings(
                            useSystemIsDark = get("useSystemIsDark", true),
                            isDark = get("isDark", false),
                            useCustomColorScheme = get("useCustomColorScheme", false),
                            animationsEnabled = get("animationsEnabled", true),
                            blurEnabled = get("blurEnabled", HazeDefaults.blurEnabled()),
                            backgroundEnabled = get("backgroundEnabled", true),
                            hapticsEnabled = get("hapticsEnabled", false),
                            showGreetings = get("showGreetings", true),
                            showNewestGrades = get("showNewestGrades", true),
                            showCurrentLesson = get("showCurrentLesson", true),
                            showYearProgress = get("showYearProgress", true),
                            showGradeHistory = get("showGradeHistory", false),
                            gradeAverageEnabled = get("gradeAverageEnabled", true),
                            gradeAverageUseWeighting = get("gradeAverageUseWeighting", false),
                            showAllSubjects = get("showAllSubjects", false),
                            showCollectionsWithoutGrades = get("showCollectionsWithoutGrades", false),
                            showAbsences = get("showAbsences", true),
                            showNotes = get("showNotes", true),
                            showTeachersWithFirstname = get("showTeachersWithFirstname", false),
                            gradeNotificationsEnabled = get("gradeNotificationsEnabled", false),
                            gradeNotificationsIntervalMinutes = get("gradeNotificationsIntervalMinutes", 60L),
                            gradeNotificationsWifiOnly = get("gradeNotificationsWifiOnly", false),
                            requireBiometricAuthentification = get("requireBiometricAuthentification", false),
                        )
                    } else {
                        null
                    }
                val gradeWeightsData =
                    if (gradeWeights) {
                        get("gradeWeightingData", GradeAverageCalculator.GradeWeightingStore())
                    } else {
                        null
                    }
                val gradeCollections =
                    if (viewModel.years.toList() == gradeYears && viewModel.allGradeCollectionsLoaded.value) {
                        viewModel.gradeCollections.toList()
                    } else if (!gradeYears.isNullOrEmpty()) {
                        viewModel.getCollections(gradeYears)
                    } else {
                        null
                    }
                val gradeYearsData =
                    if (gradeCollections != null && gradeYears != null) {
                        gradeCollections to gradeYears
                    } else {
                        null
                    }

                val date =
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.let {
                        "${it.day.toString().padStart(2, '0')}.${it.month.number.toString().padStart(2, '0')}.${it.year}"
                    }
                val exportDate =
                    ExportData(
                        containsAppSettings = appSettings,
                        containsGradeWeights = gradeWeights,
                        containsGradeYears = gradeYears != null,
                        appSettings = appSettingsData,
                        gradeWeights = gradeWeightsData,
                        gradeYears = gradeYearsData,
                    )
                @Suppress("DEPRECATION")
                FileKit.openFileSaver(
                    bytes = json.encodeToString(exportDate).toByteArray(),
                    suggestedName = "BNA_Export_vom_$date",
                    extension = "json",
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.toaster.show(
                Toast(
                    message = "Beim Exportieren ist ein Fehler aufgetreten",
                    type = ToastType.Error,
                ),
            )
        }
    }

    suspend fun importJson(
        viewModel: com.hansholz.bestenotenapp.main.ViewModel,
        applySettings: (AppSettings) -> Unit,
    ) = withContext(Dispatchers.IO) {
        try {
            val file =
                FileKit.openFilePicker(
                    type = FileKitType.File("json"),
                    title = "JSON mit Einstellungen oder Gewichtungen wählen",
                )
            file?.let { file ->
                val data = json.decodeFromString<ExportData>(file.readString())
                if (data.containsAppSettings != true && data.containsGradeWeights != true) {
                    viewModel.toaster.show(
                        Toast(
                            message = "JSON-Datei enthält keine Einstellungen oder Gewichtungen",
                            type = ToastType.Error,
                        ),
                    )
                    return@withContext
                }
                data.appSettings?.let { applySettings(it) }
                data.gradeWeights?.let { gradeAverageCalculator.saveStore(viewModel.kSafe, it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.toaster.show(
                Toast(
                    message = "Beim Wiederherstellen ist ein Fehler aufgetreten",
                    type = ToastType.Error,
                ),
            )
        }
    }
}
