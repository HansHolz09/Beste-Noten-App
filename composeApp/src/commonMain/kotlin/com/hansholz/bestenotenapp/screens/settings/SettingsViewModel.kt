package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var showIntervalDialog by mutableStateOf(false)
    var showLicenseDialog by mutableStateOf(false)
    var showConfetti by mutableStateOf(false)
}
