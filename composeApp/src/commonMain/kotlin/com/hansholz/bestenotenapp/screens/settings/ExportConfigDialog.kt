package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Archive
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.formateDate
import components.dialogs.EnhancedAlertDialog
import kotlinx.coroutines.launch
import top.ltfan.multihaptic.compose.rememberVibrator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExportConfigDialog(
    settingsViewModel: SettingsViewModel,
    viewModel: ViewModel,
) {
    val vibrator = rememberVibrator()

    var appSettings by retain { mutableStateOf(true) }
    var gradeWeights by retain { mutableStateOf(true) }
    val gradeYears = retain { mutableStateListOf<Year>() }

    var yearsLoading by remember { mutableStateOf(false) }

    LaunchedEffect(settingsViewModel.showExportConfigDialog) {
        if (settingsViewModel.showExportConfigDialog) {
            if (viewModel.years.isEmpty()) {
                yearsLoading = true
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
                yearsLoading = false
            }
        }
    }

    EnhancedAlertDialog(
        visible = settingsViewModel.showExportConfigDialog,
        onDismissRequest = { settingsViewModel.showExportConfigDialog = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    settingsViewModel.viewModelScope.launch {
                        settingsViewModel.isExporting = true
                        settingsViewModel.exportAsJson(
                            viewModel = viewModel,
                            appSettings = appSettings,
                            gradeWeights = gradeWeights,
                            gradeYears = gradeYears.sortedBy { it.id },
                        )
                        settingsViewModel.isExporting = false
                        settingsViewModel.showExportConfigDialog = false
                    }
                },
                enabled = appSettings || gradeWeights || gradeYears.isNotEmpty(),
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(
                onClick = {
                    settingsViewModel.showExportConfigDialog = false
                },
            ) {
                Text("Abbrechen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Archive, null) },
        title = { Text("Daten als JSON exportieren") },
        text = {
            EnhancedAnimatedContent(settingsViewModel.isExporting) { isExporting ->
                if (isExporting) {
                    Box(Modifier.fillMaxWidth().padding(40.dp)) {
                        ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                    }
                } else {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        appSettings = !appSettings
                                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                    }.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                EnhancedCheckbox(
                                    checked = appSettings,
                                    onCheckedChange = { appSettings = it },
                                )
                                Text(
                                    text = "App-Einstellungen",
                                    style = typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        }
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        gradeWeights = !gradeWeights
                                        vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                    }.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                EnhancedCheckbox(
                                    checked = gradeWeights,
                                    onCheckedChange = { gradeWeights = it },
                                )
                                Text(
                                    text = "Gewichtungen der Noten pro Fach",
                                    style = typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        }
                        item {
                            Text(
                                text = "Noten aus den Jahren...",
                                style = typography.titleMedium,
                                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                            )
                        }
                        item {
                            EnhancedAnimatedVisibility(
                                visible = yearsLoading,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically(),
                            ) {
                                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                                    ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                }
                            }
                        }
                        item {
                            EnhancedAnimatedVisibility(
                                visible = !yearsLoading && viewModel.years.isNotEmpty(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically(),
                            ) {
                                Column {
                                    viewModel.years.forEach { year ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    if (gradeYears.contains(year)) {
                                                        gradeYears.remove(year)
                                                    } else {
                                                        gradeYears.add(year)
                                                    }
                                                    vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                                }.padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            EnhancedCheckbox(
                                                checked = gradeYears.contains(year),
                                                onCheckedChange = {
                                                    if (it) {
                                                        gradeYears.add(year)
                                                    } else {
                                                        gradeYears.remove(year)
                                                    }
                                                },
                                            )
                                            Text(
                                                text = "${year.name} (${formateDate(year.from)} - ${formateDate(year.to)})",
                                                style = typography.bodyLarge,
                                                modifier = Modifier.padding(start = 16.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}
