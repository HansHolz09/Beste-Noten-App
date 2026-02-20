package com.hansholz.bestenotenapp.screens.grades

import com.hansholz.bestenotenapp.api.models.GradeCollection
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.roundToInt

class GradeAverageCalculator {
    @Serializable
    data class GradeWeightingStore(
        val subjects: Map<String, StoredSubjectWeightingConfig> = emptyMap(),
    )

    @Serializable
    data class StoredSubjectWeightingConfig(
        val examWeight: Int = DEFAULT_WEIGHT,
        val otherWeight: Int = DEFAULT_WEIGHT,
        val typeCategoryOverrides: Map<String, Int> = emptyMap(),
    )

    data class SubjectWeightingConfig(
        val examWeight: Int = DEFAULT_WEIGHT,
        val otherWeight: Int = DEFAULT_WEIGHT,
        val typeCategoryOverrides: Map<String, Int> = emptyMap(),
    ) {
        fun weightFor(categoryId: Int): Int =
            when (categoryId) {
                CATEGORY_EXAM -> examWeight
                else -> otherWeight
            }.coerceIn(0, MAX_WEIGHT)

        fun categoryFor(typeName: String): Int = typeCategoryOverrides[typeName] ?: if (examTypes.contains(typeName.trim().uppercase())) CATEGORY_EXAM else CATEGORY_OTHER

        fun withCategoryWeight(
            categoryId: Int,
            weight: Int,
        ): SubjectWeightingConfig =
            when (categoryId) {
                CATEGORY_EXAM -> copy(examWeight = weight.coerceIn(0, MAX_WEIGHT))
                else -> copy(otherWeight = weight.coerceIn(0, MAX_WEIGHT))
            }

        fun withTypeCategory(
            typeName: String,
            categoryId: Int,
        ): SubjectWeightingConfig {
            val validCategory = if (categoryId == CATEGORY_EXAM) CATEGORY_EXAM else CATEGORY_OTHER
            return copy(typeCategoryOverrides = typeCategoryOverrides + (typeName to validCategory))
        }
    }

    fun defaultSubjectWeightingConfig(useWeightingInsteadOfPercent: Boolean): SubjectWeightingConfig =
        if (useWeightingInsteadOfPercent) {
            SubjectWeightingConfig(
                examWeight = 1,
                otherWeight = 1,
            )
        } else {
            SubjectWeightingConfig()
        }

    data class SubjectAverageResult(
        val average: Float?,
        val hasError: Boolean,
        val weightsSum: Int,
        val ignoreWeightingValidation: Boolean,
    )

    fun calculateSubjectAverage(
        collections: List<GradeCollection>,
        weighting: SubjectWeightingConfig,
        useWeightingInsteadOfPercent: Boolean,
    ): SubjectAverageResult {
        data class CategoryAggregation(
            var sum: Int = 0,
            var count: Int = 0,
            val weight: Int,
        )

        val aggregations =
            mutableMapOf(
                CATEGORY_EXAM to CategoryAggregation(weight = weighting.weightFor(CATEGORY_EXAM)),
                CATEGORY_OTHER to CategoryAggregation(weight = weighting.weightFor(CATEGORY_OTHER)),
            )

        collections.forEach { collection ->
            val grade = parseSecondaryOneGrade(collection.grades?.getOrNull(0)?.value) ?: return@forEach
            val categoryId = if (weighting.categoryFor(collection.type) == CATEGORY_EXAM) CATEGORY_EXAM else CATEGORY_OTHER
            val aggregation = aggregations.getValue(categoryId)
            aggregation.sum += grade
            aggregation.count += 1
        }

        val activeCategories = aggregations.values.filter { it.count > 0 }
        if (activeCategories.isEmpty()) {
            return SubjectAverageResult(
                average = null,
                hasError = true,
                weightsSum = 0,
                ignoreWeightingValidation = true,
            )
        }

        var weightedSum = 0f
        var weightsSum = 0
        activeCategories.forEach { category ->
            weightedSum += category.weight * (category.sum.toFloat() / category.count.toFloat())
            weightsSum += category.weight
        }

        val ignoreWeightingValidation = activeCategories.size <= 1
        if (weightsSum == 0) {
            return SubjectAverageResult(
                average = null,
                hasError = true,
                weightsSum = weightsSum,
                ignoreWeightingValidation = ignoreWeightingValidation,
            )
        }

        val average = weightedSum / weightsSum
        val hasInvalidWeighting = !useWeightingInsteadOfPercent && !ignoreWeightingValidation && weightsSum != 100
        val hasError = average.isNaN() || hasInvalidWeighting

        return SubjectAverageResult(
            average = average,
            hasError = hasError,
            weightsSum = weightsSum,
            ignoreWeightingValidation = ignoreWeightingValidation,
        )
    }

    fun formatAverageLabel(result: SubjectAverageResult): String {
        if (result.hasError || result.average == null || result.average.isNaN()) {
            return "∅ Fehler"
        }
        return "∅ ${formatGermanDecimal(result.average)}"
    }

    fun subjectWeightingKey(
        subjectTitle: String?,
        collections: List<GradeCollection>,
    ): String {
        val subject = collections.firstNotNullOfOrNull { it.subject }

        val rawKey =
            when {
                subject?.id != null -> "id_${subject.id}"
                !subject?.localId.isNullOrBlank() -> "local_${subject.localId}"
                !subjectTitle.isNullOrBlank() -> "name_$subjectTitle"
                else -> "unknown"
            }
        return rawKey.replace(keyRegex, "_").trim('_').ifEmpty { "unknown" }
    }

    fun subjectTypeNames(collections: List<GradeCollection>): List<String> =
        collections
            .map { it.type.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

    fun loadSubjectWeighting(
        kSafe: KSafe,
        subjectKey: String,
        typeNames: Collection<String>,
        useWeightingInsteadOfPercent: Boolean,
    ): SubjectWeightingConfig {
        val stored = loadStore(kSafe).subjects[subjectKey]
        val defaultWeighting = defaultSubjectWeightingConfig(useWeightingInsteadOfPercent)
        val examWeight = (stored?.examWeight ?: defaultWeighting.examWeight).coerceIn(0, MAX_WEIGHT)
        val otherWeight = (stored?.otherWeight ?: defaultWeighting.otherWeight).coerceIn(0, MAX_WEIGHT)

        val overrides =
            (stored?.typeCategoryOverrides ?: emptyMap())
                .filterKeys { typeNames.contains(it) }
                .mapNotNull { typeName ->
                    typeName.value
                        .takeIf { it == CATEGORY_EXAM || it == CATEGORY_OTHER }
                        ?.let { typeName.key to it }
                }.toMap()

        return SubjectWeightingConfig(
            examWeight = examWeight,
            otherWeight = otherWeight,
            typeCategoryOverrides = overrides,
        )
    }

    fun persistSubjectWeighting(
        kSafe: KSafe,
        subjectKey: String,
        weighting: SubjectWeightingConfig,
    ) {
        val store = loadStore(kSafe)
        saveStore(
            kSafe = kSafe,
            store =
                store.copy(
                    subjects = store.subjects + (subjectKey to weighting.normalize().toStored()),
                ),
        )
    }

    fun clearSubjectWeighting(
        kSafe: KSafe,
        subjectKey: String,
        @Suppress("UNUSED_PARAMETER") typeNames: Collection<String>,
    ) {
        val store = loadStore(kSafe)
        if (!store.subjects.contains(subjectKey)) return
        saveStore(
            kSafe = kSafe,
            store = store.copy(subjects = store.subjects - subjectKey),
        )
    }

    fun convertStoredSubjectWeightingsMode(
        kSafe: KSafe,
        useWeightingInsteadOfPercent: Boolean,
    ) {
        val store = loadStore(kSafe)
        if (store.subjects.isEmpty()) return

        val convertedSubjects =
            store.subjects.mapValues { (_, config) ->
                val convertedWeights =
                    if (useWeightingInsteadOfPercent) {
                        convertPercentToWeight(config.examWeight, config.otherWeight)
                    } else {
                        convertWeightToPercent(config.examWeight, config.otherWeight)
                    }
                config.copy(
                    examWeight = convertedWeights.first,
                    otherWeight = convertedWeights.second,
                )
            }

        if (convertedSubjects != store.subjects) {
            saveStore(
                kSafe = kSafe,
                store = store.copy(subjects = convertedSubjects),
            )
        }
    }

    private fun parseSecondaryOneGrade(value: String?): Int? {
        if (value.isNullOrBlank()) return null
        val normalized = gradeRegex.find(value)?.value?.toIntOrNull() ?: return null
        return normalized.takeIf { it in 1..6 }
    }

    private fun formatGermanDecimal(value: Float): String {
        val scaled = (value * 100).roundToInt()
        val integerPart = scaled / 100
        val decimalPart = abs(scaled % 100).toString().padStart(2, '0')
        return "$integerPart,$decimalPart"
    }

    private fun loadStore(kSafe: KSafe): GradeWeightingStore = kSafe.getDirect(WEIGHTING_STORAGE_KEY, GradeWeightingStore())

    fun saveStore(
        kSafe: KSafe,
        store: GradeWeightingStore,
    ) {
        kSafe.putDirect(WEIGHTING_STORAGE_KEY, store)
    }

    private fun SubjectWeightingConfig.toStored(): StoredSubjectWeightingConfig =
        StoredSubjectWeightingConfig(
            examWeight = examWeight.coerceIn(0, MAX_WEIGHT),
            otherWeight = otherWeight.coerceIn(0, MAX_WEIGHT),
            typeCategoryOverrides =
                typeCategoryOverrides.filterValues { it == CATEGORY_EXAM || it == CATEGORY_OTHER },
        )

    private fun SubjectWeightingConfig.normalize(): SubjectWeightingConfig =
        copy(
            examWeight = examWeight.coerceIn(0, MAX_WEIGHT),
            otherWeight = otherWeight.coerceIn(0, MAX_WEIGHT),
            typeCategoryOverrides = typeCategoryOverrides.filterValues { it == CATEGORY_EXAM || it == CATEGORY_OTHER },
        )

    private fun convertWeightToPercent(
        examWeight: Int,
        otherWeight: Int,
    ): Pair<Int, Int> {
        val sanitizedExam = examWeight.coerceAtLeast(0)
        val sanitizedOther = otherWeight.coerceAtLeast(0)
        val sum = sanitizedExam + sanitizedOther
        if (sum <= 0) return DEFAULT_WEIGHT to DEFAULT_WEIGHT

        val examPercent = ((sanitizedExam.toDouble() / sum.toDouble()) * 100.0).roundToInt().coerceIn(0, 100)
        val otherPercent = (100 - examPercent).coerceIn(0, 100)
        return examPercent to otherPercent
    }

    private fun convertPercentToWeight(
        examPercent: Int,
        otherPercent: Int,
    ): Pair<Int, Int> {
        val sanitizedExam = examPercent.coerceAtLeast(0)
        val sanitizedOther = otherPercent.coerceAtLeast(0)
        if (sanitizedExam == 0 && sanitizedOther == 0) return 1 to 1

        val gcd = greatestCommonDivisor(sanitizedExam, sanitizedOther).coerceAtLeast(1)
        val reducedExam = (sanitizedExam / gcd).coerceIn(0, MAX_WEIGHT)
        val reducedOther = (sanitizedOther / gcd).coerceIn(0, MAX_WEIGHT)
        return if (reducedExam == 0 && reducedOther == 0) {
            1 to 1
        } else {
            reducedExam to reducedOther
        }
    }

    private tailrec fun greatestCommonDivisor(
        a: Int,
        b: Int,
    ): Int =
        when {
            a == 0 -> b
            b == 0 -> a
            else -> greatestCommonDivisor(b, a % b)
        }

    companion object {
        const val CATEGORY_EXAM = 0
        const val CATEGORY_OTHER = 1

        private const val DEFAULT_WEIGHT = 50
        private const val MAX_WEIGHT = 100
        private const val WEIGHTING_STORAGE_KEY = "gradeWeightingData"

        private val gradeRegex = Regex("\\d+")
        private val keyRegex = Regex("[^A-Za-z0-9_-]")
        private val examTypes = setOf("KL", "KLAUSUR", "KA", "KLASSENARBEIT", "K")
    }
}
