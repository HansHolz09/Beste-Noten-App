package com.hansholz.bestenotenapp.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.List
import com.hansholz.bestenotenapp.components.PreferenceItem
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.security.kSafeProviderCompose
import com.hansholz.bestenotenapp.utils.formateDate
import kotlinx.coroutines.launch
import top.ltfan.multihaptic.compose.rememberVibrator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YearSelectionDialog(
    viewModel: ViewModel,
    homeViewModel: HomeViewModel,
) = kSafeProviderCompose {
    val vibrator = rememberVibrator()

    LaunchedEffect(homeViewModel.isYearSelectionDialogShown) {
        if (homeViewModel.isYearSelectionDialogShown) {
            if (viewModel.years.isEmpty()) {
                homeViewModel.isYearSelectionDialogLoading = true
                viewModel.getYears()?.let { viewModel.years.addAll(it) }
            }
            homeViewModel.isYearSelectionDialogLoading = false
        }
    }

    EnhancedAlertDialog(
        visible = homeViewModel.isYearSelectionDialogShown,
        onDismissRequest = { homeViewModel.isYearSelectionDialogShown = false },
        confirmButton = {
            EnhancedButton(onClick = { homeViewModel.isYearSelectionDialogShown = false }) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.List, null) },
        title = { Text("Schuljahr") },
        text = {
            EnhancedAnimatedContent(homeViewModel.isYearSelectionDialogLoading) { isLoading ->
                if (isLoading) {
                    ContainedLoadingIndicator(Modifier.padding(100.dp))
                } else {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.scrollableEdgeFade(listState),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        viewModel.years.forEachIndexed { index, year ->
                            item {
                                val selected =
                                    (
                                        viewModel.user.value
                                            ?.config
                                            ?.yearId == year.id
                                    ) || (
                                        viewModel.user.value
                                            ?.config
                                            ?.yearId == null && index == viewModel.years.lastIndex
                                    )
                                PreferenceItem(
                                    title = "${year.name} (${formateDate(year.from)} - ${formateDate(year.to)})",
                                    position =
                                        when {
                                            viewModel.years.size == 1 -> PreferencePosition.Single
                                            index == 0 -> PreferencePosition.Top
                                            index == viewModel.years.lastIndex -> PreferencePosition.Bottom
                                            else -> PreferencePosition.Middle
                                        },
                                    onClick = {
                                        viewModel.viewModelScope.launch {
                                            vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                            homeViewModel.isYearSelectionDialogShown = false
                                            viewModel.setCurrentYear(if (index == viewModel.years.lastIndex) null else year.id)
                                            viewModel.reload()
                                            homeViewModel.refreshGrades(viewModel)
                                            homeViewModel.refreshTimetable(viewModel)
                                            homeViewModel.refreshStats(viewModel)
                                        }
                                    },
                                    trailingContent = { RadioButton(selected = selected, onClick = null) },
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}
