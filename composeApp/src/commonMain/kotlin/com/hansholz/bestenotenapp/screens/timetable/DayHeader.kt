package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Article
import com.hansholz.bestenotenapp.api.models.JournalNote
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.main.LocalShowNotes
import kotlinx.datetime.DayOfWeek
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
) {
    val vibrator = rememberVibrator()
    val showNotes by LocalShowNotes.current

    fun getDayAbbreviation(dayOfWeek: DayOfWeek): String =
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mo"
            DayOfWeek.TUESDAY -> "Di"
            DayOfWeek.WEDNESDAY -> "Mi"
            DayOfWeek.THURSDAY -> "Do"
            DayOfWeek.FRIDAY -> "Fr"
            DayOfWeek.SATURDAY -> "Sa"
            DayOfWeek.SUNDAY -> "So"
        }

    val isCurrentDate = (
        date ==
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
    )
    val color = if (isCurrentDate) colorScheme.primary else null

    val dayAbbreviation = getDayAbbreviation(date.dayOfWeek)
    val formattedDate = "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."

    val isNotesDialogShown = remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .padding(vertical = 8.dp)
                .clickable(null, null, !notes.isNullOrEmpty()) {
                    vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                    isNotesDialogShown.value = true
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = dayAbbreviation, color = color ?: Color.Unspecified, fontWeight = FontWeight.Bold)
            if (!notes.isNullOrEmpty() && showNotes) {
                Icon(MaterialSymbols.Rounded.Article, null, Modifier.padding(vertical = 4.dp).padding(start = 5.dp).size(16.dp))
            }
        }
        Text(text = formattedDate, color = color ?: Color.Gray)
    }

    NotesDialog(isNotesDialogShown, notes, formattedDate)
}
