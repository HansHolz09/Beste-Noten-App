package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Article
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.HomeworkStatus
import com.hansholz.bestenotenapp.main.LocalHomeworkEnabled
import com.hansholz.bestenotenapp.main.LocalShowNotes
import com.hansholz.bestenotenapp.main.ViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DayHeader(
    date: LocalDate,
    notes: List<JournalNote>?,
    captureOnly: Boolean,
    viewModel: ViewModel,
) {
    val vibrator = rememberVibrator()
    val showNotes by LocalShowNotes.current
    val homeworkEnabled by LocalHomeworkEnabled.current
    val homeworkRevision = viewModel.homeworkRevision.intValue
    var homework by remember(date) { mutableStateOf(emptyList<HomeworkEntry>()) }
    var hasManualHomework by remember(date) { mutableStateOf(false) }

    LaunchedEffect(date, homeworkEnabled, homeworkRevision) {
        homework =
            if (homeworkEnabled) {
                viewModel.getHomeworkForDate(date)
            } else {
                emptyList()
            }
        hasManualHomework = homeworkEnabled && viewModel.hasUserHomeworkForDate(date)
    }

    val isCurrentDate = (
        date ==
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
    )
    val color = if (isCurrentDate) colorScheme.primary else null

    val dayAbbreviation =
        when (date.dayOfWeek) {
            kotlinx.datetime.DayOfWeek.MONDAY -> "Mo"
            kotlinx.datetime.DayOfWeek.TUESDAY -> "Di"
            kotlinx.datetime.DayOfWeek.WEDNESDAY -> "Mi"
            kotlinx.datetime.DayOfWeek.THURSDAY -> "Do"
            kotlinx.datetime.DayOfWeek.FRIDAY -> "Fr"
            kotlinx.datetime.DayOfWeek.SATURDAY -> "Sa"
            kotlinx.datetime.DayOfWeek.SUNDAY -> "So"
        }
    val formattedDate = "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."

    val isNotesDialogShown = remember { mutableStateOf(false) }
    val allHomeworkDone = homework.isNotEmpty() && homework.all { it.status == HomeworkStatus.DONE }

    Column(
        modifier =
            Modifier
                .padding(vertical = 8.dp)
                .clickable(null, null, homeworkEnabled || (showNotes && !notes.isNullOrEmpty())) {
                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                    isNotesDialogShown.value = true
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = dayAbbreviation, color = color ?: Color.Unspecified, fontWeight = FontWeight.Bold)
            if (showNotes && !captureOnly && (!notes.isNullOrEmpty() || homework.isNotEmpty())) {
                Icon(
                    MaterialSymbols.Rounded.Article,
                    null,
                    Modifier
                        .padding(vertical = 4.dp)
                        .padding(start = 5.dp)
                        .size(20.dp)
                        .alpha(if (allHomeworkDone) 0.45f else 1f),
                    tint =
                        when {
                            allHomeworkDone -> colorScheme.onSurfaceVariant
                            hasManualHomework -> colorScheme.error
                            else -> LocalContentColor.current
                        },
                )
            }
        }
        Text(text = formattedDate, color = color ?: Color.Gray)
    }

    NotesDialog(
        visible = isNotesDialogShown,
        notes = notes,
        dateLabel = formattedDate,
        date = date,
        homework = homework,
        viewModel = viewModel,
    )
}
