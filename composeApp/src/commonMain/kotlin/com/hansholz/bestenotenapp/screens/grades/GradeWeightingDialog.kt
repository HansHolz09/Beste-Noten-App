package com.hansholz.bestenotenapp.screens.grades

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Add
import com.composables.icons.materialsymbols.rounded.Balance
import com.composables.icons.materialsymbols.rounded.Remove
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedFilterChip
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import components.dialogs.EnhancedAlertDialog

@Composable
internal fun GradeWeightingDialog(
    visible: Boolean,
    subjectTitle: String,
    collections: List<GradeCollection>,
    weighting: GradeAverageCalculator.SubjectWeightingConfig,
    useWeightingInsteadOfPercent: Boolean,
    onDismissRequest: () -> Unit,
    onCategoryWeightChanged: (categoryId: Int, weight: Int) -> Unit,
    onTypeCategoryChanged: (typeName: String, categoryId: Int) -> Unit,
    onResetToDefault: () -> Unit,
) {
    val gradeAverageCalculator = remember { GradeAverageCalculator() }
    val typeNames = remember(collections) { gradeAverageCalculator.subjectTypeNames(collections) }
    val averageResult =
        remember(collections, weighting, useWeightingInsteadOfPercent) {
            gradeAverageCalculator.calculateSubjectAverage(
                collections = collections,
                weighting = weighting,
                useWeightingInsteadOfPercent = useWeightingInsteadOfPercent,
            )
        }

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        icon = { Icon(MaterialSymbols.Rounded.Balance, null) },
        title = { Text("Gewichtungen für $subjectTitle") },
        modifier = Modifier.width(IntrinsicSize.Min),
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Aktueller Schnitt: ${gradeAverageCalculator.formatAverageLabel(averageResult)}",
                    color = colorScheme.onSurface.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
                EnhancedAnimatedVisibility(
                    visible = !useWeightingInsteadOfPercent && !averageResult.ignoreWeightingValidation && averageResult.weightsSum != 100,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Text(
                        text = "Die beiden Gewichtungen müssen zusammen 100% ergeben.",
                        color = colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = "Kategorien",
                    modifier = Modifier.padding(top = 8.dp),
                    style = typography.titleMedium,
                )
                Column {
                    WeightControlRow(
                        label = "KA/Klausur",
                        value = weighting.examWeight,
                        useWeightingInsteadOfPercent = useWeightingInsteadOfPercent,
                        onValueChange = { onCategoryWeightChanged(GradeAverageCalculator.CATEGORY_EXAM, it) },
                    )
                    WeightControlRow(
                        label = "Sonstige",
                        value = weighting.otherWeight,
                        useWeightingInsteadOfPercent = useWeightingInsteadOfPercent,
                        onValueChange = { onCategoryWeightChanged(GradeAverageCalculator.CATEGORY_OTHER, it) },
                    )
                }
                if (typeNames.isNotEmpty()) {
                    Text(
                        text = "Leistungsarten",
                        style = typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    typeNames.forEach { typeName ->
                        TypeCategoryRow(
                            typeName = typeName,
                            selectedCategory = weighting.categoryFor(typeName),
                            onCategorySelected = { onTypeCategoryChanged(typeName, it) },
                        )
                    }
                }
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(
                onClick = onResetToDefault,
            ) {
                Text("Zurücksetzen")
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismissRequest,
            ) {
                Text("Schließen")
            }
        },
    )
}

@Composable
private fun WeightControlRow(
    label: String,
    value: Int,
    useWeightingInsteadOfPercent: Boolean,
    onValueChange: (Int) -> Unit,
) {
    val step =
        remember(useWeightingInsteadOfPercent) {
            if (useWeightingInsteadOfPercent) 1 else 10
        }
    var textFieldValue by remember(value) { mutableStateOf(value.toString()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(end = 25.dp).weight(1f),
            style = typography.bodyLarge,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            EnhancedIconButton(
                onClick = {
                    onValueChange((value - step).coerceAtLeast(0))
                },
            ) {
                Icon(MaterialSymbols.Rounded.Remove, null)
            }
            BasicTextField(
                value = if (useWeightingInsteadOfPercent) textFieldValue else "$textFieldValue%",
                onValueChange = { newValue ->
                    val cleanNumber = newValue.replace("%", "").filter { it.isDigit() }
                    if (cleanNumber.isEmpty()) {
                        textFieldValue = ""
                        onValueChange(0)
                    } else {
                        val parsed = cleanNumber.toIntOrNull() ?: 0
                        if (parsed in 0..100) {
                            textFieldValue = cleanNumber
                            onValueChange(parsed)
                        }
                    }
                },
                modifier = Modifier.width(60.dp),
                textStyle =
                    typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant,
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                singleLine = true,
                cursorBrush = SolidColor(colorScheme.primary),
            )
            EnhancedIconButton(
                onClick = {
                    onValueChange((value + step).coerceAtMost(100))
                },
            ) {
                Icon(MaterialSymbols.Rounded.Add, null)
            }
        }
    }
}

@Composable
private fun TypeCategoryRow(
    typeName: String,
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = typeName,
            modifier = Modifier.weight(1f).padding(end = 20.dp),
            style = typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.width(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EnhancedFilterChip(
                selected = selectedCategory == GradeAverageCalculator.CATEGORY_EXAM,
                onClick = { onCategorySelected(GradeAverageCalculator.CATEGORY_EXAM) },
                label = { Text("KA/Klausur") },
            )
            Spacer(Modifier.width(8.dp))
            EnhancedFilterChip(
                selected = selectedCategory == GradeAverageCalculator.CATEGORY_OTHER,
                onClick = { onCategorySelected(GradeAverageCalculator.CATEGORY_OTHER) },
                label = { Text("Sonstige") },
            )
        }
    }
}

data class SubjectWeightingDialogState(
    val subjectTitle: String,
    val subjectKey: String,
    val subjectCollections: List<GradeCollection>,
)
