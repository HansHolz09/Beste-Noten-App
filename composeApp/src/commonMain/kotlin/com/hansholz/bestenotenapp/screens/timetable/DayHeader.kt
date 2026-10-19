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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Article
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedVibrator
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.HomeworkStatus
import com.hansholz.bestenotenapp.main.LocalHomeworkEnabled
import com.hansholz.bestenotenapp.main.LocalShowNotes
import com.hansholz.bestenotenapp.main.ViewModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun DayHeader(
    date: LocalDate,
    notes: List<JournalNote>?,
    captureOnly: Boolean,
    viewModel: ViewModel,
) {
    val vibrator = rememberEnhancedVibrator()
    val showNotes by LocalShowNotes.current
    val homeworkEnabled by LocalHomeworkEnabled.current
    val homeworkRevision = viewModel.homeworkRevision.intValue
    var homework by remember(date) { mutableStateOf(emptyList<HomeworkEntry>()) }

    LaunchedEffect(date, homeworkEnabled, homeworkRevision) {
        homework =
            if (homeworkEnabled) {
                viewModel.getHomeworkForDate(date)
            } else {
                emptyList()
            }
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
            DayOfWeek.MONDAY -> "Mo"
            DayOfWeek.TUESDAY -> "Di"
            DayOfWeek.WEDNESDAY -> "Mi"
            DayOfWeek.THURSDAY -> "Do"
            DayOfWeek.FRIDAY -> "Fr"
            DayOfWeek.SATURDAY -> "Sa"
            DayOfWeek.SUNDAY -> "So"
        }
    val formattedDate = "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."

    val isNotesDialogShown = remember { mutableStateOf(false) }
    val allHomeworkDone = notes.isNullOrEmpty() && homework.isNotEmpty() && homework.all { it.status == HomeworkStatus.DONE }
    val noteColor = LocalContentColor.current
    val errorColor = colorScheme.error
    val colors =
        buildList {
            notes.orEmpty().forEach { note ->
                add(
                    note.type
                        ?.color
                        ?.removePrefix("#")
                        ?.toLongOrNull(16)
                        ?.let { Color(it or 0xFF000000) } ?: noteColor,
                )
            }
            homework.sortedWith(compareBy({ it.lessonNumber ?: 0 }, { it.dueDateTime?.time }, { it.createdAt })).forEach {
                add(it.color?.let { color -> Color(color) } ?: errorColor)
            }
        }

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
                    imageVector = MaterialSymbols.Rounded.Article,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .padding(start = 5.dp)
                            .size(20.dp)
                            .alpha(if (allHomeworkDone) 0.45f else 1f)
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                            .drawWithCache {
                                val brush = Brush.linearGradient(colors.ifEmpty { listOf(noteColor) }.let { if (it.size == 1) it + it else it })
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(brush, blendMode = BlendMode.SrcIn)
                                }
                            },
                    tint = Color.White,
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
