package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.hansholz.bestenotenapp.api.models.Absence
import com.hansholz.bestenotenapp.api.models.Conductor
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.utils.formateDate
import components.dialogs.EnhancedAlertDialog

@Composable
fun AbsenceInfoDialog(
    visible: MutableState<Boolean>,
    absence: Absence,
) {
    var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

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
        icon = { Icon(Icons.Outlined.HowToReg, null) },
        title = { Text(absence.type.name) },
        text = {
            val createdBy =
                absence.histories
                    ?.find {
                        it.historyEntryType == "absence" &&
                            it.action == "created"
                    }?.conductor ?: Conductor(0, forename = "", name = "einer unbekannten Person")
            val verifiedBy =
                absence.histories
                    ?.find {
                        it.historyEntryType == "absence_verification" &&
                            it.action == "created"
                    }?.conductor ?: Conductor(0, forename = "", name = "einer unbekannten Person")

            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                        append("Zeitspanne: ")
                    }
                    append(
                        if (absence.from.takeLast(8).take(5) == "00:00" && absence.to.takeLast(8).take(5) == "23:59" && absence.from.take(10) == absence.to.take(10)) {
                            "ganztägig am ${formateDate(absence.from.take(10))}"
                        } else if ((absence.from.takeLast(8).take(5) != "00:00" || absence.to.takeLast(8).take(5) != "23:59") && absence.from.take(10) == absence.to.take(10)) {
                            "am ${formateDate(absence.from.take(10))} von ${absence.from.takeLast(8).take(5)} bis ${absence.to.takeLast(8).take(5)}"
                        } else {
                            "vom ${formateDate(absence.from.take(10))} um ${absence.from.takeLast(8).take(5)} " +
                                "bis zum ${formateDate(absence.to.take(10))} um ${absence.to.takeLast(8).take(5)}"
                        },
                    )
                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                        append("\nErstellt: ")
                    }
                    append(
                        "am ${absence.recordedAt?.let { formateDate(it.take(10)) }} " +
                            "um ${absence.recordedAt?.takeLast(8)?.take(5)} von ${if (showTeachersWithFirstname) {
                                createdBy.forename
                            } else {
                                createdBy.forename?.take(1) + "."
                            }} ${createdBy.name}",
                    )
                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                        append("\nBestätigt: ")
                    }
                    append(
                        if (absence.verification?.confirmed == true) {
                            "am ${absence.verification.recordedAt?.let { formateDate(it.take(10)) }} von ${if (showTeachersWithFirstname) {
                                verifiedBy.forename
                            } else {
                                verifiedBy.forename?.take(1) + "."
                            }} ${verifiedBy.name}"
                        } else {
                            "noch keine Bestätigung"
                        },
                    )
                },
            )
        },
    )
}
