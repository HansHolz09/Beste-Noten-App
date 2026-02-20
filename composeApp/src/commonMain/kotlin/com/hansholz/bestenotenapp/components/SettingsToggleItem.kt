package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Close
import com.composables.icons.materialsymbols.rounded.Done
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import top.ltfan.multihaptic.compose.rememberVibrator

fun LazyListScope.settingsToggleItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    position: PreferencePosition = PreferencePosition.Single,
    checkedIcon: ImageVector = MaterialSymbols.Rounded.Done,
    uncheckedIcon: ImageVector = MaterialSymbols.Rounded.Close,
    hapticsEnabled: Boolean = true,
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
            val vibrator = rememberVibrator()
            Switch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                    if (hapticsEnabled) {
                        vibrator.enhancedVibrate(
                            if (it) {
                                EnhancedVibrations.TOGGLE_ON
                            } else {
                                EnhancedVibrations.TOGGLE_OFF
                            },
                        )
                    }
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
