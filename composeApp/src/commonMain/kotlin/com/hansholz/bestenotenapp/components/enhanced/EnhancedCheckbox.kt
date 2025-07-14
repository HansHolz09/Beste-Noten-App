package com.hansholz.bestenotenapp.components.enhanced

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun EnhancedCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val hapticFeedback = LocalHapticFeedback.current

    // TODO: Redesign
    Checkbox(
        checked = checked,
        onCheckedChange = {
            onCheckedChange?.invoke(it)
            hapticFeedback.performHapticFeedback(
                if (it) {
                    HapticFeedbackType.ToggleOn
                } else {
                    HapticFeedbackType.ToggleOff
                }
            )
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}