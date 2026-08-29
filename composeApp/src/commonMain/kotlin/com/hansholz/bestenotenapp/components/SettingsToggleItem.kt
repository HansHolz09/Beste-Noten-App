package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Spacer
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
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativeSwitch
import top.ltfan.multihaptic.compose.rememberVibrator

fun LazyListScope.settingsToggleItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    icon: ImageVector? = null,
    onIconClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    position: PreferencePosition = PreferencePosition.Single,
    checkedIcon: ImageVector = MaterialSymbols.Rounded.Done,
    uncheckedIcon: ImageVector = MaterialSymbols.Rounded.Close,
    hapticsEnabled: Boolean = true,
    controlVisible: Boolean = true,
) {
    item {
        val vibrator = rememberVibrator()
        val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
        val nativeSwitch = LocalNativeSwitch.current
        PreferenceItem(
            modifier = modifier.padding(horizontal = 16.dp),
            textModifier = textModifier,
            title = text,
            icon = icon,
            onIconClick =
                if (onIconClick != null) {
                    {
                        onIconClick()
                        vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                    }
                } else {
                    null
                },
            enabled = enabled,
            position = position,
        ) {
            val change: (Boolean) -> Unit = {
                onCheckedChange(it)
                if (hapticsEnabled) {
                    vibrator.enhancedVibrateN(
                        if (it) EnhancedVibrations.TOGGLE_ON else EnhancedVibrations.TOGGLE_OFF,
                    )
                }
            }
            if (!controlVisible) {
                Spacer(Modifier.size(width = 64.dp, height = 44.dp))
            } else if (nativeComponentsEnabled && nativeSwitch != null) {
                nativeSwitch(checked, change, enabled, Modifier)
            } else {
                Switch(
                    checked = checked,
                    onCheckedChange = change,
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
}
