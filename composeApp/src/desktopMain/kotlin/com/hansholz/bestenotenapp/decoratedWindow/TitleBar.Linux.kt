package com.hansholz.bestenotenapp.decoratedWindow

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Close
import com.composables.icons.materialsymbols.rounded.Close_fullscreen
import com.composables.icons.materialsymbols.rounded.Open_in_full
import com.composables.icons.materialsymbols.rounded.Remove
import java.awt.Frame
import java.awt.event.WindowEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DecoratedWindowScope.TitleBarOnLinux(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    titleBarHeight: MutableState<Dp>,
) {
    TitleBarImpl(
        modifier,
        { _, _ -> PaddingValues(0.dp) },
        titleBarHeight,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(titleBarHeight.value),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val colors =
                if (isDark) {
                    IconButtonDefaults.filledIconButtonColors(Color(38, 40, 42), Color.LightGray)
                } else {
                    IconButtonDefaults.filledIconButtonColors(Color(227, 227, 227), Color.DarkGray)
                }
            Spacer(Modifier.weight(1f))
            FilledIconButton(
                onClick = {
                    window.extendedState = Frame.ICONIFIED
                },
                colors = colors,
            ) {
                Icon(MaterialSymbols.Rounded.Remove, null)
            }
            FilledIconButton(
                onClick = {
                    if (state.isMaximized) {
                        window.extendedState = Frame.NORMAL
                    } else {
                        window.extendedState = Frame.MAXIMIZED_BOTH
                    }
                },
                colors = colors,
            ) {
                Icon(if (state.isMaximized) MaterialSymbols.Rounded.Close_fullscreen else MaterialSymbols.Rounded.Open_in_full, null)
            }
            FilledIconButton(
                onClick = {
                    window.dispatchEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                },
                colors = colors,
            ) {
                Icon(MaterialSymbols.Rounded.Close, null)
            }
        }
    }
}
