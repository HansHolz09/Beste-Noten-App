package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import bestenotenapp.composeapp.generated.resources.Res
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import components.dialogs.EnhancedAlertDialog

@Composable
fun LibrariesDialog(settingsViewModel: SettingsViewModel) {
    val libraries by rememberLibraries {
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
        icon = { Icon(Icons.Outlined.LocalLibrary, null) },
        title = { Text("Open-Source-Lizenzen") },
        text = {
            LibrariesContainer(
                libraries = libraries,
                showDescription = true,
                licenseDialogConfirmText = "Schließen",
            )
        },
    )
}
