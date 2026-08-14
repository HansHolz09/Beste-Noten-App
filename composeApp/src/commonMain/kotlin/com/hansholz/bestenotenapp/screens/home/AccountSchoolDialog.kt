package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Apartment
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.secondaryStage
import components.dialogs.EnhancedAlertDialog
import io.ktor.http.encodeURLParameter
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun AccountSchoolDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) {
    val vibrator = rememberVibrator()
    val uriHandler = LocalUriHandler.current

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
            val user = viewModel.user.value
            val student =
                viewModel.user.value
                    ?.students
                    ?.find { it.id.toString() == viewModel.studentId.value }
            val level = viewModel.level.value
            val school = viewModel.user.value?.school
            val schoolAdress = "${school?.street} ${school?.streetNr} ${school?.city}"
            Column(Modifier.verticalScroll(rememberScrollState())) {
                SelectionContainer {
                    Text(
                        text =
                            buildAnnotatedString {
                                withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                    append("Dieser Account:")
                                }
                                append("\n• ID: ${user?.id}")
                                if (user?.email != null) {
                                    append("\n• E-Mail: ")
                                    withLink(
                                        LinkAnnotation.Clickable("mailto_user") {
                                            vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                            uriHandler.openUri("mailto:${user.email.encodeURLParameter()}")
                                        },
                                    ) {
                                        append(user.email)
                                    }
                                }
                                if (user?.username != null) {
                                    append("\n• Nutzername: ${user.username}")
                                }
                                if (user?.role != null) {
                                    append(
                                        "\n• Rolle: ${
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
                                    append("\n\nAngemeldeter Schüler:")
                                }
                                append("\n• ID: ${student?.id}")
                                if (student?.forename != null && student.name != null) {
                                    append("\n• Name: ${student.forename} ${student.name}")
                                }
                                if (level != null) {
                                    append("\n• Klassenstufe: ${level.name} (Sek. ${level.secondaryStage()?.value})")
                                }
                                if (student?.gender != null) {
                                    append(
                                        "\n• Geschlecht: ${
                                            when (student.gender) {
                                                "male" -> "männlich"
                                                "female" -> "weiblich"
                                                else -> "unbekannt"
                                            }
                                        }",
                                    )
                                }
                                if (!user?.guardians.isNullOrEmpty()) {
                                    append("\n• Eltern: ")
                                    append(user.guardians.joinToString { "${it.forename} ${it.name}" })
                                }
                                if (school?.name != null) {
                                    withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                        append("\n\n${school.name}:")
                                    }
                                    if (school.email != null) {
                                        append("\n• E-Mail: ")
                                        withLink(
                                            LinkAnnotation.Clickable("mailto_school") {
                                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                                uriHandler.openUri("mailto:${school.email.encodeURLParameter()}")
                                            },
                                        ) {
                                            append(school.email)
                                        }
                                    }
                                    if (!schoolAdress.contains(" null ")) {
                                        append("\n• Adresse: ")
                                        withLink(
                                            LinkAnnotation.Clickable("open_maps") {
                                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                                uriHandler.openUri(
                                                    "https://maps.google.com/?q=" +
                                                        "${school.name} $schoolAdress".encodeURLParameter(),
                                                )
                                            },
                                        ) {
                                            append(schoolAdress)
                                        }
                                    }
                                }
                            },
                        color = colorScheme.onSurface.copy(0.8f),
                    )
                }
            }
        },
    )
}
