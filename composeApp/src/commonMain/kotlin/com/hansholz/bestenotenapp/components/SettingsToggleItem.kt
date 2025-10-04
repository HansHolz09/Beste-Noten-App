package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

fun LazyListScope.settingsToggleItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    position: PreferencePosition = PreferencePosition.Single,
    checkedIcon: ImageVector = Icons.Outlined.Done,
    uncheckedIcon: ImageVector = Icons.Outlined.Close,
) {
    item {
        PreferenceItem(
            modifier = modifier.padding(horizontal = 16.dp),
            textModifier = textModifier,
            title = text,
            icon = icon,
            enabled = enabled,
            position = position,
        ) {
            val hapticFeedback = LocalHapticFeedback.current
            Switch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    hapticFeedback.performHapticFeedback(
                        if (it) {
                            HapticFeedbackType.ToggleOn
                        } else {
                            HapticFeedbackType.ToggleOff
                        },
                    )
                },
                thumbContent =
                    if (checked) {
                        {
                            Icon(
                                imageVector = checkedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = uncheckedIcon,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    },
                enabled = enabled,
            )
        }
    }
}
