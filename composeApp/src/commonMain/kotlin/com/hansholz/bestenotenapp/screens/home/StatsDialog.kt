package com.hansholz.bestenotenapp.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
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
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.roundToDecimals
import components.dialogs.EnhancedAlertDialog
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
            if (viewModel.years.isEmpty()) {
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            if (viewModel.currentDayStudentCount.value == null) {
                viewModel.getDayStudentCount(viewModel.years.lastOrNull())?.let { viewModel.currentDayStudentCount.value = it }
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
        icon = { Icon(Icons.Outlined.Insights, null) },
        title = { Text("Jahresinformationen") },
        text = {
            val dayData = remember(viewModel.dayStudentCount.value) { viewModel.dayStudentCount.value }
            val currentDayData = remember(viewModel.currentDayStudentCount.value) { viewModel.currentDayStudentCount.value }

            val currentLessonsNotPresentCount =
                remember(currentDayData) {
                    try {
                        currentDayData!!.lessonsNotPresentWithAbsenceCount!!.toInt() + currentDayData.lessonsNotPresentWithoutAbsenceCount!!.toInt()
                    } catch (_: Exception) {
                        0
                    }
                }
            val currentAverage =
                remember(currentDayData) {
                    try {
                        (currentDayData!!.lessonsCount!!.toFloat() / currentDayData.count!!.toFloat()).roundToDecimals(2).toString().replace('.', ',')
                    } catch (_: Exception) {
                        0
                    }
                }
            val currentPresence =
                remember(currentDayData) {
                    try {
                        (100 - currentLessonsNotPresentCount.toFloat() / currentDayData!!.lessonsCount!!.toFloat() * 100).roundToDecimals(1).toString().replace('.', ',')
                    } catch (_: Exception) {
                        0
                    }
                }
            val lessonsNotPresentCount =
                remember(currentDayData) {
                    try {
                        dayData!!.lessonsNotPresentWithAbsenceCount!!.toInt() + dayData.lessonsNotPresentWithoutAbsenceCount!!.toInt()
                    } catch (_: Exception) {
                        0
                    }
                }
            val average =
                remember(currentDayData) {
                    try {
                        (dayData!!.lessonsCount!!.toFloat() / dayData.count!!.toFloat()).roundToDecimals(2).toString().replace('.', ',')
                    } catch (_: Exception) {
                        0
                    }
                }
            val presence =
                remember(currentDayData) {
                    try {
                        (100 - lessonsNotPresentCount.toFloat() / dayData!!.lessonsCount!!.toFloat() * 100).roundToDecimals(1).toString().replace('.', ',')
                    } catch (_: Exception) {
                        0
                    }
                }
            AnimatedContent(homeViewModel.isStatsDialogLoading) { isLoading ->
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
                                    append(
                                        viewModel.intervals.joinToString("\n") { interval ->
                                            val daysRemaining = Clock.System.todayIn(TimeZone.currentSystemDefault()).daysUntil(LocalDate.parse(interval.to))
                                            interval.name.let { if (it.regionMatches(2, "HJ", 0, 2)) "${it.take(2)} ${it.substringAfter('.')}" else it } +
                                                " vom ${formateDate(interval.from)} bis ${formateDate(interval.to)}" +
                                                (if (interval.to != interval.editableTo) " mit Notenschluss am ${formateDate(interval.editableTo)}" else "") +
                                                (if (daysRemaining > 0) "\n➞ noch $daysRemaining Tage" else "")
                                        },
                                    )
                                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                        append("\n\nDaten zum aktuellen Schuljahr (${viewModel.years.lastOrNull()?.name}):\n")
                                    }
                                    append(
                                        "• Schultage: ${currentDayData?.count}\n" +
                                            "• Abwesende Tage: ${currentDayData?.notPresentCount} (davon ${currentDayData?.notPresentWithAbsenceCount} entschuldigt," +
                                            " ${currentDayData?.notPresentWithoutAbsenceCount} nicht)\n" +
                                            "• Unterrichtsstunden: ${currentDayData?.lessonsCount}\n" +
                                            "• Abwesende Stunden: $currentLessonsNotPresentCount (davon ${currentDayData?.lessonsNotPresentWithAbsenceCount}" +
                                            " entschuldigt, ${currentDayData?.lessonsNotPresentWithoutAbsenceCount} nicht)\n" +
                                            "➞ Durchschnittlich $currentAverage Stunden/Tag, $currentPresence% Anwesenheit",
                                    )
                                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                        append("\n\nGesamtübersicht:\n")
                                    }
                                    append(
                                        "• Schultage: ${dayData?.count}\n" +
                                            "• Abwesende Tage: ${dayData?.notPresentCount} (davon ${dayData?.notPresentWithAbsenceCount} entschuldigt," +
                                            " ${dayData?.notPresentWithoutAbsenceCount} nicht)\n" +
                                            "• Unterrichtsstunden: ${dayData?.lessonsCount}\n" +
                                            "• Abwesende Stunden: $lessonsNotPresentCount (davon ${dayData?.lessonsNotPresentWithAbsenceCount} entschuldigt," +
                                            " ${dayData?.lessonsNotPresentWithoutAbsenceCount} nicht)\n" +
                                            "➞ Durchschnittlich $average Stunden/Tag, $presence% Anwesenheit",
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
