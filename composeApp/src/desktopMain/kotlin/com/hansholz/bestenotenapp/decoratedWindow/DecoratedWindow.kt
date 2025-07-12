package com.hansholz.bestenotenapp.decoratedWindow

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.jetbrains.JBR
import org.jetbrains.skiko.hostOs
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

@Composable
fun DecoratedWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "",
    icon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable DecoratedWindowScope.() -> Unit,
) {
    remember {
        if (!JBR.isAvailable()) {
            error("DecoratedWindow can only be used on JetBrainsRuntime(JBR) platform")
        }
    }

    Window(
        onCloseRequest,
        state,
        visible,
        title,
        icon,
        undecorated = hostOs.isLinux,
        transparent = hostOs.isLinux,
        resizable,
        enabled,
        focusable,
        alwaysOnTop,
        onPreviewKeyEvent,
        onKeyEvent,
    ) {
        var decoratedWindowState by remember { mutableStateOf(DecoratedWindowState.of(window)) }

        DisposableEffect(window) {
            val adapter =
                object : WindowAdapter(), ComponentListener {
                    override fun windowActivated(e: WindowEvent?) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun windowDeactivated(e: WindowEvent?) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun windowIconified(e: WindowEvent?) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun windowDeiconified(e: WindowEvent?) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun windowStateChanged(e: WindowEvent) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun componentResized(e: ComponentEvent?) {
                        decoratedWindowState = DecoratedWindowState.of(window)
                    }

                    override fun componentMoved(e: ComponentEvent?) {
                        // Empty
                    }

                    override fun componentShown(e: ComponentEvent?) {
                        // Empty
                    }

                    override fun componentHidden(e: ComponentEvent?) {
                        // Empty
                    }
                }

            window.addWindowListener(adapter)
            window.addWindowStateListener(adapter)
            window.addComponentListener(adapter)

            onDispose {
                window.removeWindowListener(adapter)
                window.removeWindowStateListener(adapter)
                window.removeComponentListener(adapter)
            }
        }

        CompositionLocalProvider(LocalTitleBarInfo provides TitleBarInfo(title, icon)) {
            val scope =
                object : DecoratedWindowScope {
                    override val state: DecoratedWindowState
                        get() = decoratedWindowState

                    override val window: ComposeWindow
                        get() = this@Window.window
                }
            CompositionLocalProvider(LocalDecoratedWindowScope provides scope) {
                Box(if (hostOs.isLinux) Modifier.clip(RoundedCornerShape(8.dp)).border(2.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(2.dp) else Modifier) {
                    scope.content()
                }
            }
        }
    }
}

@Stable
interface DecoratedWindowScope : FrameWindowScope {
    override val window: ComposeWindow

    val state: DecoratedWindowState
}


@Immutable
@JvmInline
value class DecoratedWindowState(val state: ULong) {
    val isActive: Boolean
        get() = state and Active != 0UL

    val isFullscreen: Boolean
        get() = state and Fullscreen != 0UL

    val isMinimized: Boolean
        get() = state and Minimize != 0UL

    val isMaximized: Boolean
        get() = state and Maximize != 0UL

    override fun toString(): String = "${javaClass.simpleName}(isFullscreen=$isFullscreen, isActive=$isActive)"

    companion object {
        val Active: ULong = 1UL shl 0
        val Fullscreen: ULong = 1UL shl 1
        val Minimize: ULong = 1UL shl 2
        val Maximize: ULong = 1UL shl 3

        fun of(
            fullscreen: Boolean = false,
            minimized: Boolean = false,
            maximized: Boolean = false,
            active: Boolean = true,
        ): DecoratedWindowState =
            DecoratedWindowState(
                (if (fullscreen) Fullscreen else 0UL) or
                    (if (minimized) Minimize else 0UL) or
                    (if (maximized) Maximize else 0UL) or
                    (if (active) Active else 0UL)
            )

        fun of(window: ComposeWindow): DecoratedWindowState =
            of(
                fullscreen = window.placement == WindowPlacement.Fullscreen,
                minimized = window.isMinimized,
                maximized = window.placement == WindowPlacement.Maximized,
                active = window.isActive,
            )
    }
}

internal data class TitleBarInfo(val title: String, val icon: Painter?)

internal val LocalTitleBarInfo: ProvidableCompositionLocal<TitleBarInfo> = compositionLocalOf {
    error("LocalTitleBarInfo not provided, TitleBar must be used in DecoratedWindow")
}

internal val LocalDecoratedWindowScope: ProvidableCompositionLocal<DecoratedWindowScope> = compositionLocalOf {
    error("LocalDecoratedWindowState not provided, DecoratedWindow must be used")
}
