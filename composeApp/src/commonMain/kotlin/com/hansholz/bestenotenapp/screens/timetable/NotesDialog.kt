package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Article
import com.composables.icons.materialsymbols.rounded.News
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import components.dialogs.EnhancedAlertDialog

@Composable
fun NotesDialog(
    visible: MutableState<Boolean>,
    notes: List<JournalNote>?,
    date: String,
) {
    EnhancedAlertDialog(
        visible = visible.value,
        onDismissRequest = { visible.value = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    visible.value = false
                },
            ) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Article, null) },
        title = { Text("Notizen vom $date") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                itemsIndexed(notes ?: emptyList()) { index, note ->
                    PreferenceItem(
                        title = note.description ?: "$note",
                        icon = MaterialSymbols.Rounded.News,
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
                    )
                }
            }
        },
    )
}
