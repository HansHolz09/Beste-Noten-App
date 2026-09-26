package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bestenotenapp.composeapp.generated.resources.Res
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Local_library
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.preferenceShape
import com.hansholz.bestenotenapp.components.scrollableEdgeFade
import com.mikepenz.aboutlibraries.ui.compose.DefaultChipColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.m3.style.m3VariantColors
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryRow

@Composable
fun LibrariesDialog(settingsViewModel: SettingsViewModel) {
    val libraries by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }
    EnhancedAlertDialog(
        visible = settingsViewModel.showLicenseDialog,
        onDismissRequest = { settingsViewModel.showLicenseDialog = false },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    settingsViewModel.showLicenseDialog = false
                },
            ) {
                Text("Schließen")
            }
        },
        icon = { Icon(MaterialSymbols.Rounded.Local_library, null) },
        title = { Text("Open-Source-Lizenzen") },
        text = {
            val listState = rememberLazyListState()
            LibrariesContainer(
                libraries = libraries,
                modifier = Modifier.scrollableEdgeFade(listState),
                lazyListState = listState,
                detailMode = LibraryDetailMode.Sheet,
                colors =
                    LibraryDefaults.libraryColors(
                        libraryBackgroundColor = Color.Transparent,
                        libraryContentColor = colorScheme.onBackground,
                        versionChipColors =
                            DefaultChipColors(
                                containerColor = colorScheme.background,
                                contentColor = colorScheme.onBackground,
                            ),
                        dialogBackgroundColor = colorScheme.background,
                    ),
                variantColors =
                    LibraryDefaults.m3VariantColors(
                        rowBackground = Color.Transparent,
                        rowExpandedBackground = Color.Transparent,
                    ),
                libraryRow = { index, library, expanded, toggle, style ->
                    val lastIndex = libraries?.libraries.orEmpty().lastIndex
                    val position =
                        when {
                            lastIndex == 0 -> PreferencePosition.Single
                            index == 0 -> PreferencePosition.Top
                            index == lastIndex -> PreferencePosition.Bottom
                            else -> PreferencePosition.Middle
                        }
                    Box(
                        Modifier
                            .padding(bottom = if (index == lastIndex) 0.dp else 2.dp)
                            .clip(position.preferenceShape())
                            .background(colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)),
                    ) {
                        LibraryRow(library, expanded, toggle, style)
                    }
                },
                licenseDialogConfirmText = "Schließen",
            )
        },
    )
}
