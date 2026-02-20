package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Insights
import com.composables.icons.materialsymbols.rounded.Refresh
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalTime::class)
@Composable
fun StatsDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) {
    val density = LocalDensity.current

    LaunchedEffect(homeViewModel.isStatsDialogShown) {
        if (homeViewModel.isStatsDialogShown && (viewModel.intervals.isEmpty() || viewModel.dayStudentCount.value == null)) {
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
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            if (viewModel.currentDayStudentCount.value == null) {
                viewModel.getDayStudentCount(viewModel.years.lastOrNull())?.let { viewModel.currentDayStudentCount.value = it }
            }
            if (viewModel.currentLessonStudentCount.value == null) {
                viewModel.getLessonStudentCount(viewModel.years.lastOrNull())?.let { viewModel.currentLessonStudentCount.value = it }
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
                            viewModel.getYears()?.let {
                                viewModel.years.clear()
                                viewModel.years.addAll(it)
                            }
                            viewModel.getDayStudentCount(viewModel.years.lastOrNull())?.let { viewModel.currentDayStudentCount.value = it }
                            viewModel.getLessonStudentCount(viewModel.years.lastOrNull())?.let { viewModel.currentLessonStudentCount.value = it }
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
            val dayData = remember(viewModel.dayStudentCount.value) { viewModel.dayStudentCount.value }
            val lessonData = remember(viewModel.lessonStudentCount.value) { viewModel.lessonStudentCount.value }
            val currentDayData = remember(viewModel.currentDayStudentCount.value) { viewModel.currentDayStudentCount.value }
            val currentLessonData = remember(viewModel.currentLessonStudentCount.value) { viewModel.currentLessonStudentCount.value }

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
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        var width by remember { mutableStateOf(0.dp) }
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
                                        append("\n\nDaten zum aktuellen Schuljahr (${viewModel.years.lastOrNull()?.name}):\n")
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
                                },
                            modifier = Modifier.onGloballyPositioned { with(density) { width = it.size.width.toDp() } },
                            color = colorScheme.onSurface.copy(0.8f),
                        )
                        Spacer(Modifier.height(25.dp))
                        Text(
                            text = "Hinweis: Obwohl diese Daten direkt von beste.schule stammen, besteht keine Sicherheit, dass alle der Informationen korrekt sind.",
                            modifier = Modifier.width(width),
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        },
    )
}
