package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Add
import com.composables.icons.materialsymbols.rounded.Add_task
import com.composables.icons.materialsymbols.rounded.Close
import com.composables.icons.materialsymbols.rounded.Delete
import com.composables.icons.materialsymbols.rounded.Done
import com.composables.icons.materialsymbols.rounded.Drag_indicator
import com.composables.icons.materialsymbols.rounded.Edit_note
import com.composables.icons.materialsymbols.rounded.News
import com.composables.icons.materialsymbols.rounded.Task_alt
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.homework.HomeworkPlacement
import com.hansholz.bestenotenapp.homework.HomeworkSource
import com.hansholz.bestenotenapp.homework.HomeworkStatus
import com.hansholz.bestenotenapp.homework.HomeworkType
import com.hansholz.bestenotenapp.homework.newHomeworkId
import com.hansholz.bestenotenapp.main.LocalHomeworkGoogleSyncEnabled
import com.hansholz.bestenotenapp.main.LocalHomeworkTypes
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import components.dialogs.EnhancedAlertDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun LazyListScope.homeworkItems(
    homework: List<HomeworkEntry>,
    onEdit: (HomeworkEntry) -> Unit,
    onDone: (HomeworkEntry) -> Unit,
    enabled: Boolean = true,
    loadingId: String? = null,
    followingItems: Int = 0,
) {
    itemsIndexed(homework) { index, entry ->
        val done = entry.status == HomeworkStatus.DONE
        PreferenceItem(
            title = entry.title,
            subtitle =
                buildList {
                    add(entry.type.label)
                    if (done) add("erledigt")
                    entry.reminderAt?.let { add(it.formatReminder()) }
                    entry.description?.let { add("\n" + it) }
                }.joinToString(" · "),
            icon = MaterialSymbols.Rounded.News,
            enabled = enabled,
            onClick = { onEdit(entry) },
            modifier = Modifier.alpha(if (done) 0.5f else 1f),
            titleMaxLines = 4,
            position =
                when {
                    homework.size + followingItems <= 1 -> PreferencePosition.Single
                    index == 0 -> PreferencePosition.Top
                    index == homework.size + followingItems - 1 -> PreferencePosition.Bottom
                    else -> PreferencePosition.Middle
                },
            backgroundColor = colorScheme.surfaceContainerLow,
        ) {
            Box(contentAlignment = Alignment.Center) {
                EnhancedIconButton(
                    onClick = { onDone(entry) },
                    enabled = enabled,
                ) {
                    Icon(
                        MaterialSymbols.Rounded.Done,
                        null,
                        tint = if (done) colorScheme.primary else colorScheme.onSurfaceVariant,
                    )
                }
                if (loadingId == entry.localId) {
                    CircularWavyProgressIndicator(Modifier.size(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeworkEditorDialog(
    visible: MutableState<Boolean>,
    initialEntry: HomeworkEntry?,
    dueDate: LocalDate,
    createEntry: (String, String?, HomeworkType) -> HomeworkEntry,
    onSave: suspend (HomeworkEntry) -> Unit,
    onDelete: (suspend (HomeworkEntry) -> Unit)? = null,
) = kSafeProviderCompose {
    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var title by remember(initialEntry, visible.value) { mutableStateOf(initialEntry?.title.orEmpty()) }
    var description by remember(initialEntry, visible.value) { mutableStateOf(initialEntry?.description.orEmpty()) }
    val homeworkTypesState = LocalHomeworkTypes.current
    val homeworkTypes = homeworkTypesState.value
    var type by remember(initialEntry, visible.value) { mutableStateOf(initialEntry?.type ?: homeworkTypes.firstOrNull() ?: HomeworkType.HOMEWORK) }
    var typeEditorVisible by remember(visible.value) { mutableStateOf(false) }
    var reminderAt by remember(initialEntry, visible.value) { mutableStateOf(initialEntry?.reminderAt) }
    var selectedReminderDate by remember(initialEntry, visible.value) { mutableStateOf(initialEntry?.reminderAt?.date) }
    var datePickerVisible by remember(visible.value) { mutableStateOf(false) }
    var timePickerVisible by remember(visible.value) { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }
    val busy = saving || deleting
    val reminderError =
        reminderAt?.let {
            val timeZone = TimeZone.currentSystemDefault()
            val minutesBefore =
                (
                    LocalDateTime(dueDate, LocalTime(0, 0)).toInstant(timeZone) -
                        it.toInstant(timeZone)
                ).inWholeMinutes
            when {
                minutesBefore < 0 -> "Die Erinnerung muss an einem Tag zuvor liegen."
                it <= Clock.System.now().toLocalDateTime(timeZone) -> "Die Erinnerung muss in der Zukunft liegen."
                minutesBefore !in 0..40_320L -> "Die Erinnerung darf höchstens 4 Wochen vor der Fälligkeit liegen."
                else -> null
            }
        }

    EnhancedAlertDialog(
        visible = visible.value,
        onDismissRequest = { if (!busy) visible.value = false },
        icon = { Icon(if (initialEntry == null) MaterialSymbols.Rounded.Add_task else MaterialSymbols.Rounded.Task_alt, null) },
        title = { Text(if (initialEntry == null) "Eintrag hinzufügen" else "Eintrag bearbeiten") },
        text = {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .focusRequester(focusRequester)
                        .focusable(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Titel") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    enabled = !busy,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Beschreibung") },
                    minLines = 2,
                    enabled = !busy,
                )
                val typeScrollState = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .scrollableEdgeFade(
                                canScrollBackward = typeScrollState.canScrollBackward,
                                canScrollForward = typeScrollState.canScrollForward,
                                orientation = Orientation.Horizontal,
                            ).horizontalScroll(typeScrollState),
                ) {
                    val displayedTypes = if (type in homeworkTypes) homeworkTypes else listOf(type) + homeworkTypes
                    displayedTypes.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = {
                                type = option
                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            },
                            label = { Text(option.label) },
                            enabled = !busy,
                        )
                    }
                    EnhancedIconButton(
                        onClick = { typeEditorVisible = true },
                        enabled = !busy,
                    ) {
                        Icon(MaterialSymbols.Rounded.Edit_note, null)
                    }
                }
                if (LocalHomeworkGoogleSyncEnabled.current.value) {
                    Row {
                        EnhancedOutlinedButton(
                            onClick = {
                                focusRequester.requestFocus()
                                keyboardController?.hide()
                                datePickerVisible = true
                            },
                            enabled = !busy,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(reminderAt?.formatReminder() ?: "Erinnerung einrichten", Modifier.padding(start = 8.dp))
                        }
                        EnhancedAnimatedVisibility(reminderAt != null) {
                            EnhancedOutlinedButton(
                                onClick = {
                                    reminderAt = null
                                    selectedReminderDate = null
                                },
                                modifier = Modifier.padding(start = 10.dp),
                                enabled = !busy,
                            ) {
                                Icon(MaterialSymbols.Rounded.Close, null)
                            }
                        }
                    }
                    EnhancedAnimatedContent(reminderError) { reminderError ->
                        reminderError?.let {
                            Text(it, color = colorScheme.error, style = typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Box(contentAlignment = Alignment.Center) {
                EnhancedButton(
                    onClick = {
                        focusRequester.requestFocus()
                        keyboardController?.hide()
                        scope.launch {
                            saving = true
                            try {
                                val entry =
                                    initialEntry?.copy(
                                        title = title.trim(),
                                        description = description.trim().ifBlank { null },
                                        type = type,
                                        reminderAt = reminderAt,
                                    ) ?: createEntry(title.trim(), description.trim().ifBlank { null }, type).copy(reminderAt = reminderAt)
                                onSave(entry)
                                visible.value = false
                            } finally {
                                saving = false
                            }
                        }
                    },
                    enabled = !busy && title.isNotBlank() && reminderError == null,
                ) {
                    Text("Speichern")
                }
                EnhancedAnimatedVisibility(saving) {
                    CircularWavyProgressIndicator(Modifier.size(32.dp))
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (initialEntry != null && onDelete != null) {
                    Box(contentAlignment = Alignment.Center) {
                        EnhancedOutlinedButton(
                            onClick = {
                                focusRequester.requestFocus()
                                keyboardController?.hide()
                                scope.launch {
                                    deleting = true
                                    try {
                                        onDelete(initialEntry)
                                        visible.value = false
                                    } finally {
                                        deleting = false
                                    }
                                }
                            },
                            enabled = !busy,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                            border = BorderStroke(1.dp, colorScheme.onErrorContainer),
                        ) {
                            Text("Löschen")
                        }
                        this@Row.EnhancedAnimatedVisibility(deleting) {
                            CircularWavyProgressIndicator(Modifier.size(32.dp))
                        }
                    }
                }
                EnhancedOutlinedButton(
                    onClick = {
                        focusRequester.requestFocus()
                        keyboardController?.hide()
                        visible.value = false
                    },
                    enabled = !busy,
                ) {
                    Text("Abbrechen")
                }
            }
        },
    )

    val initialDate = reminderAt?.date ?: dueDate.minus(DatePeriod(days = 1))
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
        )
    EnhancedAlertDialog(
        visible = datePickerVisible,
        withBlur = false,
        onDismissRequest = { datePickerVisible = false },
        title = { Text("Datum wählen") },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    selectedReminderDate =
                        datePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                        }
                    datePickerVisible = false
                    if (selectedReminderDate != null) timePickerVisible = true
                },
                enabled = datePickerState.selectedDateMillis != null,
            ) {
                Text("Weiter")
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(onClick = { datePickerVisible = false }) {
                Text("Abbrechen")
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                title = {},
            )
        },
    )

    val initialTime = reminderAt?.time ?: LocalTime(18, 0)
    val timePickerState =
        rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true,
        )
    EnhancedAlertDialog(
        visible = timePickerVisible,
        withBlur = false,
        onDismissRequest = { timePickerVisible = false },
        title = { Text("Uhrzeit wählen") },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    selectedReminderDate?.let {
                        reminderAt = LocalDateTime(it, LocalTime(timePickerState.hour, timePickerState.minute))
                    }
                    timePickerVisible = false
                },
            ) {
                Text("Übernehmen")
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(onClick = { timePickerVisible = false }) {
                Text("Abbrechen")
            }
        },
        text = { TimePicker(state = timePickerState) },
    )

    HomeworkTypeEditorDialog(
        visible = typeEditorVisible,
        types = homeworkTypes,
        onDismissRequest = { typeEditorVisible = false },
        onSave = { updatedTypes ->
            homeworkTypesState.value = updatedTypes
            put("homeworkTypes", updatedTypes.map(HomeworkType::value))
            typeEditorVisible = false
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeworkTypeEditorDialog(
    visible: Boolean,
    types: List<HomeworkType>,
    onDismissRequest: () -> Unit,
    onSave: (List<HomeworkType>) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val draft = remember(visible) { types.toMutableStateList() }
    val listState = rememberLazyListState()
    var newType by remember(visible) { mutableStateOf("") }
    var draggedType by remember(visible) { mutableStateOf<HomeworkType?>(null) }
    var draggedOffset by remember(visible) { mutableFloatStateOf(0f) }

    fun addType() {
        val label = newType.trim()
        val type = HomeworkType(label)
        if (label.isBlank() || draft.any { it == type || it.label.equals(label, ignoreCase = true) }) return
        draft += type
        newType = ""
        scope.launch {
            listState.animateScrollToItem(draft.lastIndex)
        }
    }

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        icon = { Icon(MaterialSymbols.Rounded.Edit_note, null) },
        title = { Text("Eintragstypen bearbeiten") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .weight(1f, false)
                            .scrollableEdgeFade(
                                canScrollBackward = listState.canScrollBackward,
                                canScrollForward = listState.canScrollForward,
                                orientation = Orientation.Vertical,
                            ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(draft, key = HomeworkType::value) { option ->
                        val index = draft.indexOf(option)
                        PreferenceItem(
                            title = option.label,
                            icon = MaterialSymbols.Rounded.Drag_indicator,
                            position =
                                when {
                                    draft.size == 1 -> PreferencePosition.Single
                                    index == 0 -> PreferencePosition.Top
                                    index == draft.lastIndex -> PreferencePosition.Bottom
                                    else -> PreferencePosition.Middle
                                },
                            modifier =
                                Modifier
                                    .then(if (draggedType == option) Modifier else Modifier.animateItem())
                                    .zIndex(if (draggedType == option) 1f else 0f)
                                    .graphicsLayer {
                                        translationY = if (draggedType == option) draggedOffset else 0f
                                    }.pointerInput(option) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggedType = option
                                                draggedOffset = 0f
                                                vibrator.enhancedVibrateN(EnhancedVibrations.TICK)
                                            },
                                            onDragEnd = {
                                                draggedType = null
                                                draggedOffset = 0f
                                                vibrator.enhancedVibrateN(EnhancedVibrations.TICK)
                                            },
                                            onDragCancel = {
                                                draggedType = null
                                                draggedOffset = 0f
                                            },
                                        ) { _, dragAmount ->
                                            draggedOffset += dragAmount.y
                                            val currentInfo =
                                                listState.layoutInfo.visibleItemsInfo.firstOrNull {
                                                    it.key == option.value
                                                } ?: return@detectDragGesturesAfterLongPress
                                            val draggedCenter = currentInfo.offset + currentInfo.size / 2 + draggedOffset
                                            val scrollDelta =
                                                when {
                                                    draggedCenter < listState.layoutInfo.viewportStartOffset -> -24f
                                                    draggedCenter > listState.layoutInfo.viewportEndOffset -> 24f
                                                    else -> 0f
                                                }
                                            if (scrollDelta != 0f) {
                                                scope.launch {
                                                    draggedOffset += listState.scrollBy(scrollDelta)
                                                }
                                            }
                                            val targetInfo =
                                                listState.layoutInfo.visibleItemsInfo.firstOrNull {
                                                    draggedCenter.toInt() in it.offset..(it.offset + it.size)
                                                } ?: return@detectDragGesturesAfterLongPress
                                            if (targetInfo.key != option.value) {
                                                val from = draft.indexOf(option)
                                                val target = draft.indexOfFirst { it.value == targetInfo.key }
                                                if (from >= 0 && target >= 0) {
                                                    val wasAtAbsoluteTop =
                                                        listState.firstVisibleItemIndex == 0 &&
                                                            listState.firstVisibleItemScrollOffset == 0
                                                    draft.add(target, draft.removeAt(from))
                                                    draggedOffset += currentInfo.offset - targetInfo.offset
                                                    if (wasAtAbsoluteTop) {
                                                        listState.requestScrollToItem(0)
                                                    }
                                                }
                                                vibrator.enhancedVibrateN(EnhancedVibrations.LOW_TICK)
                                            }
                                        }
                                    },
                        ) {
                            IconButton(
                                onClick = {
                                    draft.remove(option)
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                },
                            ) {
                                Icon(MaterialSymbols.Rounded.Delete, null)
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = newType,
                    onValueChange = { newType = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Neuer Eintragstyp") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addType() }),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                addType()
                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            },
                            enabled =
                                newType.isNotBlank() &&
                                    draft.none {
                                        it == HomeworkType(newType.trim()) || it.label.equals(newType.trim(), ignoreCase = true)
                                    },
                        ) {
                            Icon(MaterialSymbols.Rounded.Add, null)
                        }
                    },
                )
            }
        },
        confirmButton = {
            EnhancedButton(onClick = { onSave(draft.toList()) }) {
                Text("Fertig")
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(onClick = onDismissRequest) {
                Text("Abbrechen")
            }
        },
    )
}

fun newDayHomeworkEntry(
    date: LocalDate,
    title: String,
    description: String?,
    type: HomeworkType,
): HomeworkEntry {
    val now = Clock.System.now()
    return HomeworkEntry(
        localId = newHomeworkId(),
        title = title,
        description = description,
        type = type,
        status = HomeworkStatus.OPEN,
        placement = HomeworkPlacement.DAY,
        dueDate = date,
        dueDateTime = null,
        subjectId = null,
        subjectName = null,
        teacherId = null,
        roomId = null,
        timetableId = null,
        timetableTimeId = null,
        timetableTimeLessonId = null,
        lessonNumber = null,
        weekday = date.dayOfWeek.ordinal + 1,
        sourceDate = date,
        source = HomeworkSource.USER,
        createdAt = now,
        updatedAt = now,
        deletedAt = null,
    )
}

fun newLessonHomeworkEntry(
    date: LocalDate,
    lesson: JournalLesson,
    timetableId: String?,
    title: String,
    description: String?,
    type: HomeworkType,
): HomeworkEntry {
    val now = Clock.System.now()
    return HomeworkEntry(
        localId = newHomeworkId(),
        title = title,
        description = description,
        type = type,
        status = HomeworkStatus.OPEN,
        placement = HomeworkPlacement.LESSON,
        dueDate = date,
        dueDateTime = null,
        subjectId = lesson.subject?.id?.toString(),
        subjectName = lesson.subject?.name,
        teacherId =
            lesson.teachers
                ?.firstOrNull()
                ?.id
                ?.toString(),
        roomId =
            lesson.rooms
                ?.firstOrNull()
                ?.id
                ?.toString(),
        timetableId = timetableId,
        timetableTimeId = null,
        timetableTimeLessonId = lesson.homeworkLessonId(),
        lessonNumber = lesson.nr.toIntOrNull(),
        weekday = date.dayOfWeek.ordinal + 1,
        sourceDate = date,
        source = HomeworkSource.USER,
        createdAt = now,
        updatedAt = now,
        deletedAt = null,
    )
}

private fun LocalDateTime.formatReminder(): String =
    "Erinnerung: " +
        "${day.toString().padStart(2, '0')}.${month.number.toString().padStart(2, '0')}.$year " +
        "um ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
