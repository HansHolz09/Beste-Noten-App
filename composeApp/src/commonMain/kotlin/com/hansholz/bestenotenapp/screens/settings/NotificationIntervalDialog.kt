package com.hansholz.bestenotenapp.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.main.LocalGradeNotificationIntervalMinutes
import com.hansholz.bestenotenapp.notifications.GradeNotifications
import com.hansholz.bestenotenapp.utils.formateInterval
import com.russhwolf.settings.Settings
import components.dialogs.EnhancedAlertDialog

@Composable
fun NotificationIntervalDialog(settingsViewModel: SettingsViewModel) {
    val hapticFeedback = LocalHapticFeedback.current

    var notificationIntervalMinutes by LocalGradeNotificationIntervalMinutes.current
    val settings = Settings()

    val intervalOptions = remember { listOf(15L, 30L, 60L, 120L, 360L, 720L, 1440L) }
    EnhancedAlertDialog(
        visible = GradeNotifications.isSupported && settingsViewModel.showIntervalDialog,
        onDismissRequest = { settingsViewModel.showIntervalDialog = false },
        icon = { Icon(Icons.Outlined.History, null) },
        title = { Text("Überprüfungsintervall") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                intervalOptions.forEach { option ->
                    item {
                        Row(
                            Modifier
                                .height(56.dp)
                                .fillParentMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .selectable(
                                    selected = (notificationIntervalMinutes == option),
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        notificationIntervalMinutes = option
                                        settings.putLong("gradeNotificationsIntervalMinutes", option)
                                        GradeNotifications.onSettingsUpdated()
                                        settingsViewModel.showIntervalDialog = false
                                    },
                                    role = Role.RadioButton,
                                ).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (notificationIntervalMinutes == option),
                                onClick = null,
                            )
                            Text(
                                text = formateInterval(option),
                                style = typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(onClick = { settingsViewModel.showIntervalDialog = false }) {
                Text("Schließen")
            }
        },
    )
}
