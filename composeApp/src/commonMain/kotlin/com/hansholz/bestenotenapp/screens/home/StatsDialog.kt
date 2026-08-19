package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Insights
import com.composables.icons.materialsymbols.rounded.Refresh
import com.hansholz.bestenotenapp.api.models.JournalLessonStudentBySlot
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.appendWithSymbols
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.roundToDecimals
import com.hansholz.bestenotenapp.utils.tryRemember
import components.dialogs.EnhancedAlertDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatsDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) {
    LaunchedEffect(homeViewModel.isStatsDialogShown) {
        if (
            homeViewModel.isStatsDialogShown &&
            (
                viewModel.intervals.isEmpty() ||
                    viewModel.dayStudentCount.value == null ||
                    viewModel.lessonStudentBySlot.isEmpty()
            )
        ) {
            homeViewModel.isStatsDialogLoading = true
            if (viewModel.intervals.isEmpty()) {
                viewModel.getIntervals()?.let { viewModel.intervals.addAll(it) }
            }
            if (viewModel.dayStudentCount.value == null) {
                viewModel.getDayStudentCount()?.let { viewModel.dayStudentCount.value = it }
            }
            if (viewModel.lessonStudentCount.value == null) {
                viewModel.getLessonStudentCount()?.let { viewModel.lessonStudentCount.value = it }
            }
            if (viewModel.lessonStudentBySlot.isEmpty()) {
                viewModel.getLessonStudentBySlot()?.let { viewModel.lessonStudentBySlot.addAll(it) }
            }
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            if (viewModel.currentDayStudentCount.value == null) {
                viewModel.getDayStudentCount(viewModel.user.value?.year)?.let { viewModel.currentDayStudentCount.value = it }
            }
            if (viewModel.currentLessonStudentCount.value == null) {
                viewModel.getLessonStudentCount(viewModel.user.value?.year)?.let { viewModel.currentLessonStudentCount.value = it }
            }
            if (viewModel.currentLessonStudentBySlot.isEmpty()) {
                viewModel.getLessonStudentBySlot(viewModel.user.value?.year)?.let { viewModel.currentLessonStudentBySlot.addAll(it) }
            }
            homeViewModel.isStatsDialogLoading = false
        }
    }

    EnhancedAlertDialog(
        visible = homeViewModel.isStatsDialogShown,
        onDismissRequest = { homeViewModel.isStatsDialogShown = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    homeViewModel.isStatsDialogShown = false
                },
            ) {
                Text("Schließen")
            }
        },
        dismissButton = {
            EnhancedAnimatedVisibility(!homeViewModel.isStatsDialogLoading) {
                EnhancedOutlinedButton(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            homeViewModel.isStatsDialogLoading = true
                            viewModel.getIntervals()?.let {
                                viewModel.intervals.clear()
                                viewModel.intervals.addAll(it)
                            }
                            viewModel.getDayStudentCount()?.let { viewModel.dayStudentCount.value = it }
                            viewModel.getLessonStudentCount()?.let { viewModel.lessonStudentCount.value = it }
                            viewModel.getLessonStudentBySlot()?.let {
                                viewModel.lessonStudentBySlot.clear()
                                viewModel.lessonStudentBySlot.addAll(it)
                            }
                            viewModel.getYears()?.let {
                                viewModel.years.clear()
                                viewModel.years.addAll(it)
                            }
                            viewModel.getDayStudentCount(viewModel.user.value?.year)?.let { viewModel.currentDayStudentCount.value = it }
                            viewModel.getLessonStudentCount(viewModel.user.value?.year)?.let { viewModel.currentLessonStudentCount.value = it }
                            viewModel.getLessonStudentBySlot(viewModel.user.value?.year)?.let {
                                viewModel.currentLessonStudentBySlot.clear()
                                viewModel.currentLessonStudentBySlot.addAll(it)
                            }
                            homeViewModel.isStatsDialogLoading = false
                        }
                    },
                ) {
                    Icon(MaterialSymbols.Rounded.Refresh, null)
                }
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Insights, null) },
        title = { Text("Jahresinformationen") },
        text = {
            val dayData = viewModel.dayStudentCount.value
            val lessonData = viewModel.lessonStudentCount.value
            val currentDayData = viewModel.currentDayStudentCount.value
            val currentLessonData = viewModel.currentLessonStudentCount.value

            val currentAverage =
                tryRemember(currentLessonData, currentDayData) {
                    (currentLessonData!!.count!!.toFloat() / currentDayData!!.count!!.toFloat()).roundToDecimals(2).toString().replace('.', ',')
                }
            val currentPresence =
                tryRemember(currentLessonData, currentDayData) {
                    (100 - currentLessonData!!.notPresentCount!!.toFloat() / currentDayData!!.lessonsCount!!.toFloat() * 100).roundToDecimals(1).toString().replace('.', ',')
                }
            val currentDaysNotPresentWithoutAbsenceCount =
                tryRemember(currentDayData) {
                    currentDayData!!.notPresentCount!! - currentDayData.notPresentWithAbsenceCount!!
                }
            val currentLessonsNotPresentWithoutAbsenceCount =
                tryRemember(currentLessonData) {
                    currentLessonData?.notPresentCount!! - currentLessonData.notPresentWithAbsenceCount!!
                }
            val average =
                tryRemember(lessonData, dayData) {
                    (lessonData!!.count!!.toFloat() / dayData!!.count!!.toFloat()).roundToDecimals(2).toString().replace('.', ',')
                }
            val presence =
                tryRemember(lessonData, dayData) {
                    (100 - lessonData!!.notPresentCount!!.toFloat() / dayData!!.lessonsCount!!.toFloat() * 100).roundToDecimals(1).toString().replace('.', ',')
                }
            val daysNotPresentWithoutAbsenceCount =
                tryRemember(dayData) {
                    dayData!!.notPresentCount!! - dayData.notPresentWithAbsenceCount!!
                }
            val lessonsNotPresentWithoutAbsenceCount =
                tryRemember(lessonData) {
                    lessonData?.notPresentCount!! - lessonData.notPresentWithAbsenceCount!!
                }
            EnhancedAnimatedContent(homeViewModel.isStatsDialogLoading) { isLoading ->
                if (isLoading) {
                    ContainedLoadingIndicator(Modifier.padding(100.dp))
                } else {
                    val scrollState = rememberScrollState()
                    Column(Modifier.scrollableEdgeFade(scrollState).verticalScroll(scrollState)) {
                        SelectionContainer {
                            Text(
                                text =
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                            append("Zeiträume:\n")
                                        }
                                        appendWithSymbols(
                                            viewModel.intervals.joinToString("\n") { interval ->
                                                val daysRemaining = Clock.System.todayIn(TimeZone.currentSystemDefault()).daysUntil(LocalDate.parse(interval.to))
                                                interval.name.let { if (it.regionMatches(2, "HJ", 0, 2)) "${it.take(2)} ${it.substringAfter('.')}" else it } +
                                                    " vom ${formateDate(interval.from)} bis ${formateDate(interval.to)}" +
                                                    (if (interval.to != interval.editableTo) " mit Notenschluss am ${formateDate(interval.editableTo)}" else "") +
                                                    (if (daysRemaining > 1) "\n➜ noch $daysRemaining Tage" else "")
                                            },
                                        )
                                        withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                            append("\n\nDaten zum Schuljahr (${viewModel.user.value?.year?.name}):\n")
                                        }
                                        appendWithSymbols(
                                            "• Schultage: ${currentDayData?.count}\n" +
                                                "• Abwesende Tage: ${currentDayData?.notPresentCount} (davon ${currentDayData?.notPresentWithAbsenceCount} entschuldigt," +
                                                " $currentDaysNotPresentWithoutAbsenceCount nicht)\n" +
                                                "• Unterrichtsstunden: ${currentLessonData?.count}\n" +
                                                "• Abwesende Stunden: ${currentLessonData?.notPresentCount} (davon ${currentLessonData?.notPresentWithAbsenceCount}" +
                                                " entschuldigt, $currentLessonsNotPresentWithoutAbsenceCount nicht)\n" +
                                                "➜ Durchschnittlich $currentAverage Stunden/Tag, $currentPresence% Anwesenheit",
                                        )
                                        withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                            append("\n\nGesamtübersicht:\n")
                                        }
                                        appendWithSymbols(
                                            "• Schultage: ${dayData?.count}\n" +
                                                "• Abwesende Tage: ${dayData?.notPresentCount} (davon ${dayData?.notPresentWithAbsenceCount} entschuldigt," +
                                                " $daysNotPresentWithoutAbsenceCount nicht)\n" +
                                                "• Unterrichtsstunden: ${lessonData?.count}\n" +
                                                "• Abwesende Stunden: ${lessonData?.notPresentCount} (davon ${lessonData?.notPresentWithAbsenceCount} entschuldigt," +
                                                " $lessonsNotPresentWithoutAbsenceCount nicht)\n" +
                                                "➜ Durchschnittlich $average Stunden/Tag, $presence% Anwesenheit",
                                        )
                                        if (viewModel.currentLessonStudentBySlot.isNotEmpty()) {
                                            withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                                append("\n\nAnwesenheit nach Stunden (${viewModel.user.value?.year?.name}):")
                                            }
                                        }
                                    },
                                color = colorScheme.onSurface.copy(0.8f),
                            )
                        }
                        if (viewModel.currentLessonStudentBySlot.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            LessonsBySlotView(viewModel.currentLessonStudentBySlot)
                        }
                        if (viewModel.lessonStudentBySlot.isNotEmpty()) {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = "Anwesenheit nach Stunden (gesamte Zeit):",
                                color = colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(10.dp))
                            LessonsBySlotView(viewModel.lessonStudentBySlot)
                        }
                        Spacer(Modifier.height(25.dp))
                        val annotatedString =
                            buildAnnotatedString {
                                append("Hinweis: Obwohl diese Daten direkt von ")
                                if (viewModel.isDemoAccount.value) {
                                    pushStringAnnotation(tag = "strikethrough_tag", annotation = "thick_line")
                                    append("beste.schule")
                                    pop()
                                    append(" Demodaten ")
                                } else {
                                    append("beste.schule ")
                                }
                                append("stammen, besteht keine Sicherheit, dass alle Informationen korrekt sind.")
                            }
                        var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                        val strikeThroughColor = colorScheme.onSurfaceVariant
                        Text(
                            text = annotatedString,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        textLayoutResult?.let { layout ->
                                            annotatedString
                                                .getStringAnnotations("strikethrough_tag", 0, annotatedString.length)
                                                .forEach { annotation ->
                                                    val path = layout.getPathForRange(annotation.start, annotation.end)
                                                    val rect = path.getBounds()
                                                    drawLine(
                                                        color = strikeThroughColor,
                                                        start = Offset(rect.left + 5, rect.center.y),
                                                        end = Offset(rect.right, rect.center.y),
                                                        strokeWidth = 1.5.dp.toPx(),
                                                        cap = StrokeCap.Round,
                                                    )
                                                }
                                        }
                                    },
                            onTextLayout = { textLayoutResult = it },
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun LessonsBySlotView(lessonStudentBySlot: List<JournalLessonStudentBySlot>) {
    Row {
        lessonStudentBySlot.groupBy { it.weekday }.forEach {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text =
                        when (it.key) {
                            1 -> "Mo"
                            2 -> "Di"
                            3 -> "Mi"
                            4 -> "Do"
                            5 -> "Fr"
                            else -> ""
                        },
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                it.value.forEach {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().padding(5.dp),
                        colors =
                            CardDefaults.outlinedCardColors(
                                containerColor =
                                    Color.Red.copy(((it.count ?: 1).toFloat() / (it.presentCount ?: 1).toFloat() - 1)),
                            ),
                        border =
                            BorderStroke(
                                width = 2.dp,
                                color = colorScheme.outline,
                            ),
                    ) {
                        Text(
                            text = "${it.presentCount}/${it.count}",
                            modifier = Modifier.padding(5.dp).height(20.dp).align(Alignment.CenterHorizontally),
                            autoSize = TextAutoSize.StepBased(stepSize = 5.sp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
