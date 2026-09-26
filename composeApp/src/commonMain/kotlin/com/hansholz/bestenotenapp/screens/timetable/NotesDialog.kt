package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Add
import com.composables.icons.materialsymbols.rounded.Article
import com.composables.icons.materialsymbols.rounded.News
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.components.PreferenceCategory
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.HomeworkStatus
import com.hansholz.bestenotenapp.main.ViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import top.ltfan.multihaptic.compose.rememberVibrator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesDialog(
    visible: MutableState<Boolean>,
    notes: List<JournalNote>?,
    dateLabel: String,
    date: LocalDate,
    homework: List<HomeworkEntry>,
    viewModel: ViewModel,
) {
    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val editorVisible = remember { mutableStateOf(false) }
    val editedHomework = remember { mutableStateOf<HomeworkEntry?>(null) }
    val busyEntryId = remember { mutableStateOf<String?>(null) }

    EnhancedAlertDialog(
        visible = visible.value,
        onDismissRequest = { if (busyEntryId.value == null) visible.value = false },
        confirmButton = {
            EnhancedButton(
                onClick = { visible.value = false },
                enabled = busyEntryId.value == null,
            ) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Article, null) },
        title = { Text("Notizen vom $dateLabel") },
        text = {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier.scrollableEdgeFade(listState),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (!notes.isNullOrEmpty()) {
                    item {
                        PreferenceCategory("beste.schule", reduceTopPadding = true)
                    }
                }
                itemsIndexed(notes ?: emptyList()) { index, note ->
                    PreferenceItem(
                        title = note.description ?: "$note",
                        icon = MaterialSymbols.Rounded.News,
                        enabled = busyEntryId.value == null,
                        titleMaxLines = 10,
                        position =
                            if (notes!!.size == 1) {
                                PreferencePosition.Single
                            } else if (notes.size > 2) {
                                if (index != 0 && index != notes.size - 1) {
                                    PreferencePosition.Middle
                                } else if (index == 0) {
                                    PreferencePosition.Top
                                } else {
                                    PreferencePosition.Bottom
                                }
                            } else {
                                if (index == 0) {
                                    PreferencePosition.Top
                                } else {
                                    PreferencePosition.Bottom
                                }
                            },
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                }
                item {
                    PreferenceCategory("Hausaufgaben", reduceTopPadding = notes.isNullOrEmpty())
                }
                if (homework.isEmpty()) {
                    item {
                        PreferenceItem(
                            title = "Keine Einträge",
                            icon = MaterialSymbols.Rounded.Article,
                            enabled = busyEntryId.value == null,
                            position = PreferencePosition.Top,
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        )
                    }
                } else {
                    homeworkItems(
                        homework = homework,
                        onEdit = {
                            editedHomework.value = it
                            editorVisible.value = true
                            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                        },
                        onDone = { entry ->
                            scope.launch {
                                busyEntryId.value = entry.localId
                                try {
                                    viewModel.markHomeworkDone(entry.localId, entry.status != HomeworkStatus.DONE)
                                } finally {
                                    busyEntryId.value = null
                                }
                            }
                        },
                        enabled = busyEntryId.value == null,
                        loadingId = busyEntryId.value,
                        followingItems = 1,
                    )
                }
                item {
                    PreferenceItem(
                        title = "Eintrag hinzufügen",
                        icon = MaterialSymbols.Rounded.Add,
                        onClick =
                            {
                                editedHomework.value = null
                                editorVisible.value = true
                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            }.takeIf { busyEntryId.value == null },
                        enabled = busyEntryId.value == null,
                        position = PreferencePosition.Bottom,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                }
            }
        },
    )

    HomeworkEditorDialog(
        visible = editorVisible,
        initialEntry = editedHomework.value,
        dueDate = date,
        createEntry = { title, description, type ->
            newDayHomeworkEntry(
                date = date,
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
