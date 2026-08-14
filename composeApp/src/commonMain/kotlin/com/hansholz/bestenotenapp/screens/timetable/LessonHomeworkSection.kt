package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Add
import com.composables.icons.materialsymbols.rounded.Done
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.HomeworkStatus
import com.hansholz.bestenotenapp.homework.HomeworkType
import com.hansholz.bestenotenapp.main.ViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import top.ltfan.multihaptic.compose.rememberVibrator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LessonHomeworkSection(
    viewModel: ViewModel,
    lesson: JournalLesson,
    sourceLessons: List<JournalLesson> = listOf(lesson),
    selectedDay: LocalDate,
) {
    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val editorVisible = remember { mutableStateOf(false) }
    val editedHomework = remember { mutableStateOf<HomeworkEntry?>(null) }
    val homework = remember { mutableStateOf(emptyList<HomeworkEntry>()) }
    val busyEntryId = remember { mutableStateOf<String?>(null) }
    val homeworkRevision = viewModel.homeworkRevision.intValue

    LaunchedEffect(sourceLessons, selectedDay, homeworkRevision) {
        homework.value =
            sourceLessons
                .flatMap { sourceLesson ->
                    val lessonId = sourceLesson.homeworkLessonId() ?: return@flatMap emptyList()
                    viewModel.getHomeworkForLesson(lessonId, selectedDay).filter { it.belongsToCurrentLessonSubject(sourceLesson) }
                }.distinctBy { it.localId }
    }

    HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
    ListItem(
        headlineContent = { Text("Hausaufgaben und Notizen") },
        supportingContent = {
            Text(
                if (homework.value.isEmpty()) {
                    "Keine stundenspezifischen Einträge"
                } else {
                    "${homework.value.size} Eintrag/Einträge"
                },
            )
        },
        trailingContent = {
            EnhancedIconButton(
                onClick = {
                    editedHomework.value = null
                    editorVisible.value = true
                },
                enabled = busyEntryId.value == null,
            ) {
                Icon(MaterialSymbols.Rounded.Add, null)
            }
        },
        colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
    )

    homework.value.forEach { entry ->
        val done = entry.status == HomeworkStatus.DONE
        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
        ListItem(
            headlineContent = { Text(entry.title) },
            overlineContent = {
                Text(
                    when (entry.type) {
                        HomeworkType.HOMEWORK -> "Hausaufgabe"
                        HomeworkType.TEST -> "Test"
                        HomeworkType.APPOINTMENT -> "Termin"
                        HomeworkType.NOTE -> "Notiz"
                    },
                )
            },
            supportingContent = { entry.description?.let { Text(it) } },
            modifier =
                Modifier
                    .alpha(if (done) 0.5f else 1f)
                    .clickable(enabled = busyEntryId.value == null) {
                        editedHomework.value = entry
                        editorVisible.value = true
                        vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                    },
            trailingContent = {
                Box(contentAlignment = Alignment.Center) {
                    EnhancedIconButton(
                        enabled = busyEntryId.value == null,
                        onClick = {
                            scope.launch {
                                busyEntryId.value = entry.localId
                                try {
                                    viewModel.markHomeworkDone(entry.localId, entry.status != HomeworkStatus.DONE)
                                } finally {
                                    busyEntryId.value = null
                                }
                            }
                        },
                    ) {
                        Icon(
                            MaterialSymbols.Rounded.Done,
                            null,
                            tint = if (done) colorScheme.primary else colorScheme.onSurfaceVariant,
                        )
                    }
                    EnhancedAnimatedVisibility(busyEntryId.value == entry.localId) {
                        CircularWavyProgressIndicator(Modifier.size(32.dp))
                    }
                }
            },
            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
        )
    }

    HomeworkEditorDialog(
        visible = editorVisible,
        initialEntry = editedHomework.value,
        dueDate = selectedDay,
        createEntry = { title, description, type ->
            newLessonHomeworkEntry(
                date = selectedDay,
                lesson = lesson,
                timetableId = viewModel.currentTimetable.value?.id,
                title = title,
                description = description,
                type = type,
            )
        },
        onSave = { entry ->
            if (editedHomework.value == null) {
                viewModel.createHomework(entry)
            } else {
                viewModel.updateHomework(entry)
            }
        },
        onDelete = { viewModel.deleteHomework(it.localId) },
    )
}
