package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Calendar_clock
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.ViewModel
import components.dialogs.EnhancedAlertDialog

@Composable
fun TimesDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) {
    LaunchedEffect(homeViewModel.isTimesDialogShown) {
        if (homeViewModel.isTimesDialogShown && viewModel.times.isEmpty()) {
            homeViewModel.isTimesDialogLoading = true
            if (viewModel.times.isEmpty()) {
                viewModel.getTimes()?.let { viewModel.times.addAll(it) }
            }
        }
        homeViewModel.isTimesDialogLoading = false
    }

    EnhancedAlertDialog(
        visible = homeViewModel.isTimesDialogShown,
        onDismissRequest = { homeViewModel.isTimesDialogShown = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    homeViewModel.isTimesDialogShown = false
                },
            ) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Calendar_clock, null) },
        title = { Text("Unterrichtszeiten") },
        text = {
            EnhancedAnimatedContent(homeViewModel.isTimesDialogLoading) { isLoading ->
                if (isLoading) {
                    ContainedLoadingIndicator(Modifier.padding(90.dp))
                } else if (viewModel.times.isEmpty()) {
                    Text("Es sind keine Unterrichtszeiten verfügbar.")
                } else {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text =
                                buildAnnotatedString {
                                    viewModel.times.forEachIndexed { index, times ->
                                        if (index > 0) append("\n\n")
                                        withStyle(SpanStyle(colorScheme.onSurface, fontWeight = FontWeight.Bold)) {
                                            append("${times.name}:")
                                        }
                                        times.lessons.forEach { lesson ->
                                            append("\n${lesson.nr}. Stunde: ${lesson.from}-${lesson.to}")
                                        }
                                    }
                                },
                            color = colorScheme.onSurface.copy(0.8f),
                        )
                    }
                }
            }
        },
    )
}
