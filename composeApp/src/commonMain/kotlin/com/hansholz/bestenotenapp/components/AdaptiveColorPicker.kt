package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAlertDialog
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.main.LocalNativeColorPicker

@Composable
fun AdaptiveColorPicker(
    color: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    onVisibilityChanged: (Boolean) -> Unit = {},
) {
    val nativePicker = LocalNativeColorPicker.current
    var visible by remember { mutableStateOf(false) }
    var anchorBounds by remember { mutableStateOf(Rect.Zero) }
    var selectedColor by remember(visible) { mutableStateOf(color) }
    if (visible) {
        DisposableEffect(Unit) {
            onVisibilityChanged(true)
            onDispose { onVisibilityChanged(false) }
        }
    }
    EnhancedIconButton(
        onClick = { visible = true },
        modifier =
            modifier
                .size(40.dp)
                .onGloballyPositioned { anchorBounds = it.boundsInWindow() }
                .semantics { contentDescription = "Farbe wählen" },
    ) {
        Box(Modifier.size(24.dp).background(color, CircleShape))
    }
    if (nativePicker != null) {
        if (visible && !anchorBounds.isEmpty) {
            nativePicker(color, anchorBounds, onColorChanged) { visible = false }
        }
        return
    }
    EnhancedAlertDialog(
        visible = visible,
        withBlur = false,
        onDismissRequest = { visible = false },
        title = { Text("Farbe wählen") },
        text = {
            val controller = rememberColorPickerController()
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HsvColorPicker(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    controller = controller,
                    initialColor = color,
                    onColorChanged = { selectedColor = it.color },
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    controller = controller,
                    initialColor = color,
                )
                Box(Modifier.fillMaxWidth().height(32.dp).background(selectedColor, CircleShape))
            }
        },
        confirmButton = {
            EnhancedButton(onClick = {
                onColorChanged(selectedColor)
                visible = false
            }) {
                Text("Übernehmen")
            }
        },
        dismissButton = {
            EnhancedOutlinedButton(onClick = { visible = false }) {
                Text("Abbrechen")
            }
        },
    )
}
