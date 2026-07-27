package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Apartment
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.ViewModel
import components.dialogs.EnhancedAlertDialog
import io.ktor.http.encodeURLParameter

@Composable
fun AccountSchoolDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) {
    EnhancedAlertDialog(
        visible = homeViewModel.isAccountSchoolDialogShown,
        onDismissRequest = { homeViewModel.isAccountSchoolDialogShown = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    homeViewModel.isAccountSchoolDialogShown = false
                },
            ) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Apartment, null) },
        title = { Text("Account- und Schuldaten") },
        text = {
            val user = remember { viewModel.user.value }
            val student =
                remember {
                    viewModel.user.value
                        ?.students
                        ?.find { it.id.toString() == viewModel.studentId.value }
                }
            val level = remember { viewModel.level.value }
            val school = remember { viewModel.user.value?.school }
            val schoolAdress = remember { "${school?.street} ${school?.streetNr} ${school?.city}" }
            val times = remember { school?.times?.getOrNull(0) }
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                append("Dieser Account:\n")
                            }
                            append("• ID: ${user?.id}\n")
                            if (user?.email != null) {
                                append("• E-Mail: ")
                                withLink(LinkAnnotation.Url("mailto:${user.email.encodeURLParameter()}")) {
                                    append("${user.email}\n")
                                }
                            }
                            if (user?.username != null) {
                                append("• Nutzername: ${user.username}\n")
                            }
                            if (user?.role != null) {
                                append(
                                    "• Rolle: ${
                                        when (user.role) {
                                            "student" -> "Schüler"
                                            "guardian" -> "Elternteil"
                                            "teacher" -> "Lehrer"
                                            "management" -> "Manager"
                                            "mod" -> "Moderator"
                                            "admin" -> "Administrator"
                                            else -> "unbekannt"
                                        }
                                    }",
                                )
                            }
                            withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                append("\n\nAngemeldeter Schüler:\n")
                            }
                            append("• ID: ${student?.id}\n")
                            if (student?.forename != null && student.name != null) {
                                append("• Name: ${student.forename} ${student.name}\n")
                            }
                            if (level != null) {
                                append("• Klassenstufe: ${level.name} (${level.intervalType})\n")
                            }
                            if (student?.gender != null) {
                                append(
                                    "• Geschlecht: ${
                                        when (student.gender) {
                                            "male" -> "männlich"
                                            "female" -> "weiblich"
                                            else -> "unbekannt"
                                        }
                                    }\n",
                                )
                            }
                            if (!user?.guardians.isNullOrEmpty()) {
                                append("• Eltern: ")
                                append(user.guardians.joinToString { "${it.forename} ${it.name}" })
                            }
                            if (school?.name != null) {
                                withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                    append("\n\n${school.name}:\n")
                                }
                                if (school.email != null) {
                                    append("• E-Mail: ")
                                    withLink(LinkAnnotation.Url("mailto:${school.email.encodeURLParameter()}")) {
                                        append("${school.email}\n")
                                    }
                                }
                                if (!schoolAdress.contains(" null ")) {
                                    append("• Adresse: ")
                                    withLink(
                                        LinkAnnotation.Url(
                                            "https://maps.google.com/?q=" +
                                                "${school.name} $schoolAdress".encodeURLParameter(),
                                        ),
                                    ) {
                                        append(schoolAdress)
                                    }
                                }
                            }
                            if (times != null) {
                                withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                    append("\n\n${times.name} (${times.type}):")
                                }
                                times.lessons.forEach { lesson ->
                                    append("\n${lesson.nr}. Stunde: ${lesson.from}-${lesson.to}")
                                }
                            }
                        },
                    color = colorScheme.onSurface.copy(0.8f),
                )
            }
        },
    )
}
