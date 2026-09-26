package com.hansholz.bestenotenapp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativeDatePicker
import com.hansholz.bestenotenapp.main.LocalNativeTimePicker

@Composable
fun AdaptiveDatePicker(
    selectedDateMillis: Long?,
    onSelectedDateChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
    materialContent: @Composable () -> Unit,
) {
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val nativePicker = LocalNativeDatePicker.current
    if (nativeComponentsEnabled && nativePicker != null) {
        nativePicker(selectedDateMillis, onSelectedDateChanged, modifier)
    } else {
        materialContent()
    }
}

@Composable
fun AdaptiveTimePicker(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    materialContent: @Composable () -> Unit,
) {
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val nativePicker = LocalNativeTimePicker.current
    if (nativeComponentsEnabled && nativePicker != null) {
        nativePicker(hour, minute, onTimeChanged, modifier)
    } else {
        materialContent()
    }
}
