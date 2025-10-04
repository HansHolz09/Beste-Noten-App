package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import com.hansholz.bestenotenapp.decoratedWindow.CustomTitleBarObject
import com.hansholz.bestenotenapp.decoratedWindow.LocalDecoratedWindowScope
import com.jetbrains.JBR
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jetbrains.skiko.hostOs
import java.awt.Frame
import java.awt.event.MouseEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.customTitleBarMouseEventHandler(): Modifier =
    if (hostOs.isLinux) {
        var lastPress = 0L
        val viewConfig = LocalViewConfiguration.current
        val window = LocalDecoratedWindowScope.current.window

        this.onPointerEvent(PointerEventType.Press, PointerEventPass.Main) {
            if (
                this.currentEvent.button == PointerButton.Primary &&
                this.currentEvent.changes.any { changed -> !changed.isConsumed }
            ) {
                JBR.getWindowMove()?.startMovingTogetherWithMouse(window, MouseEvent.BUTTON1)
                if (
                    System.currentTimeMillis() - lastPress in
                    viewConfig.doubleTapMinTimeMillis..viewConfig.doubleTapTimeoutMillis
                ) {
                    if (window.extendedState == Frame.MAXIMIZED_BOTH) {
                        window.extendedState = Frame.NORMAL
                    } else {
                        window.extendedState = Frame.MAXIMIZED_BOTH
                    }
                }
                lastPress = System.currentTimeMillis()
            }
        }
    } else {
        this.pointerInput(Unit) {
            val currentContext = currentCoroutineContext()
            awaitPointerEventScope {
                var inUserControl = false
                while (currentContext.isActive) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    event.changes.forEach {
                        if (!it.isConsumed && !inUserControl) {
                            CustomTitleBarObject.customTitleBar?.forceHitTest(false)
                        } else {
                            if (event.type == PointerEventType.Press) {
                                inUserControl = true
                            }
                            if (event.type == PointerEventType.Release) {
                                inUserControl = false
                            }
                            CustomTitleBarObject.customTitleBar?.forceHitTest(true)
                        }
                    }
                }
            }
        }
    }
