package com.hansholz.bestenotenapp.data

import bestenotenapp.composeApp.BuildConfig
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.screens.grades.GradeAverageCalculator
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val appVersionCode: String? = BuildConfig.VERSION_CODE,
    val containsAppSettings: Boolean? = null,
    val containsGradeWeights: Boolean? = null,
    val containsGradeYears: Boolean? = null,
    val appSettings: AppSettings? = null,
    val gradeWeights: GradeAverageCalculator.GradeWeightingStore? = null,
    val gradeYears: Pair<List<GradeCollection>, List<Year>>? = null,
)

@Serializable
data class AppSettings(
    val useSystemIsDark: Boolean = true,
    val isDark: Boolean = false,
    val useCustomColorScheme: Boolean = false,
    val animationsEnabled: Boolean = true,
    val blurEnabled: Boolean = false,
    val backgroundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = false,
    val showGreetings: Boolean = true,
    val showNewestGrades: Boolean = true,
    val showCurrentLesson: Boolean = true,
    val showYearProgress: Boolean = true,
    val showGradeHistory: Boolean = false,
    val gradeAverageEnabled: Boolean = true,
    val gradeAverageUseWeighting: Boolean = false,
    val showAllSubjects: Boolean = false,
    val showCollectionsWithoutGrades: Boolean = false,
    val showAbsences: Boolean = true,
    val showNotes: Boolean = true,
    val showTeachersWithFirstname: Boolean = false,
    val gradeNotificationsEnabled: Boolean = false,
    val gradeNotificationsIntervalMinutes: Long = 60L,
    val gradeNotificationsWifiOnly: Boolean = false,
    val requireBiometricAuthentification: Boolean = false,
)
