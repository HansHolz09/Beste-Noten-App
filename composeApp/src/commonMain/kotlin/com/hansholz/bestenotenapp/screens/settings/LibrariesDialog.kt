package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import bestenotenapp.composeapp.generated.resources.Res
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Local_library
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.mikepenz.aboutlibraries.ui.compose.DefaultChipColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import components.dialogs.EnhancedAlertDialog

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
            LibrariesContainer(
                libraries = libraries,
                showDescription = true,
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
                licenseDialogConfirmText = "Schließen",
            )
        },
    )
}
