package com.hansholz.bestenotenapp.utils

fun normalizeGrade(gradeValue: String?): String {
    if (gradeValue.isNullOrBlank()) return "N/A"

    val regex = Regex("\\d+")
    return regex.find(gradeValue)?.value ?: "N/A"
}
