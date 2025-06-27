package com.hansholz.bestenotenapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hansholz.bestenotenapp.decoratedWindow.DecoratedWindow
import com.hansholz.bestenotenapp.decoratedWindow.TitleBar
import com.hansholz.bestenotenapp.main.*
import com.jetbrains.JBR
import io.github.kdroidfilter.platformtools.darkmodedetector.mac.setMacOsAdaptiveTitleBar
import org.jetbrains.skiko.hostOs
import java.awt.Color
import java.awt.Dimension

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class, ExperimentalMaterial3Api::class)
fun main() {
    setMacOsAdaptiveTitleBar()
    application {
        DecoratedWindow(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1200.dp, 800.dp)),
            title = "Beste-Noten-App",
        ) {
            val density = LocalDensity.current
            window.minimumSize = Dimension(with(density) { 800.toDp().roundToPx() }, with(density) { 600.toDp().roundToPx() })
            val titleBarHeight = remember { mutableStateOf(20.dp) }
            var isDark by remember { mutableStateOf(false) }
            CompositionLocalProvider(
                LocalTitleBarModifier provides Modifier.onGloballyPositioned { titleBarHeight.value = with(density) { it.size.height.toDp() } },
                LocalNavigationDrawerTopPadding provides if (getExactPlatform() == ExactPlatform.MACOS) titleBarHeight.value else null
            ) {
                App(
                    isDark = { isDark = it },
                    colors = {
                        if (JBR.isRoundedCornersManagerSupported()) {
                            JBR.getRoundedCornersManager().setRoundedCorners(
                                window,
                                if (hostOs.isWindows) "full" else if (hostOs.isMacOS) arrayOf(20f, 2, Color(it.outline.toArgb())) else null
                            )
                        }
                    }
                )
            }
            TitleBar(isDark = isDark, titleBarHeight = titleBarHeight)
        }
    }
}
