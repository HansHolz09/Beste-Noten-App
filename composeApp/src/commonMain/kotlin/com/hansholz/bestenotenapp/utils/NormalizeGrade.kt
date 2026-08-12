package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.Level

enum class SecondaryStage(
    val value: Int,
) {
    ONE(1),
    TWO(2),
}

data class GradeScale(
    val best: Int,
    val worst: Int,
) {
    val min = minOf(best, worst)
    val max = maxOf(best, worst)

    fun contains(value: Float): Boolean = value in min.toFloat()..max.toFloat()

    fun quality(value: Float): Float = ((value - worst) / (best - worst).toFloat()).coerceIn(0f, 1f)
}

fun Level.secondaryStage(): SecondaryStage? =
    when {
        intervalType.contains("2") -> SecondaryStage.TWO
        intervalType.contains("11") -> SecondaryStage.TWO
        intervalType.contains("II") -> SecondaryStage.TWO
        intervalType.contains("I") -> SecondaryStage.ONE
        intervalType.contains("1") -> SecondaryStage.ONE
        else -> null
    }

fun Level.gradeScale(): GradeScale? {
    val defaultScale =
        when (secondaryStage()) {
            SecondaryStage.ONE -> GradeScale(best = 1, worst = 6)
            SecondaryStage.TWO -> GradeScale(best = 15, worst = 0)
            null -> null
        }
    val best = bestGrade ?: defaultScale?.best ?: return null
    val worst = worstGrade ?: defaultScale?.worst ?: return null
    return GradeScale(best, worst).takeIf { best != worst }
}

fun parseGradeValue(gradeValue: String?): Float? {
    if (gradeValue.isNullOrBlank()) return null
    return gradeRegex
        .find(gradeValue)
        ?.value
        ?.replace(',', '.')
        ?.toFloatOrNull()
}

fun normalizeGrade(gradeValue: String?): String = parseGradeValue(gradeValue)?.let { if (it % 1f == 0f) it.toInt().toString() else it.toString() } ?: "N/A"

private val gradeRegex = Regex("\\d+(?:[.,]\\d+)?")
