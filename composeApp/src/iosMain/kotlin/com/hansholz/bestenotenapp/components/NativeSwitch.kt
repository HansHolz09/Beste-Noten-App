package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UISwitch
import platform.UIKit.UIView
import platform.darwin.NSObject
import kotlin.time.Duration.Companion.milliseconds

private val activeNativeSwitchContainers = mutableSetOf<UIView>()
private var nativeIosSwitchesSuppressed = false

internal fun hideNativeSwitches() {
    nativeIosSwitchesSuppressed = true
    activeNativeSwitchContainers.forEach { it.hidden = true }
    hideNativeAppearanceButtons()
}

internal fun showNativeSwitches() {
    nativeIosSwitchesSuppressed = false
    activeNativeSwitchContainers.forEach { it.hidden = false }
    showNativeAppearanceButtons()
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier,
    fadeIn: Boolean,
) {
    val scope = rememberCoroutineScope()
    val target = remember { SwitchTarget(scope, onCheckedChange) }
    val fadeVisible = remember(fadeIn) { mutableStateOf(!fadeIn) }
    target.onCheckedChange = onCheckedChange
    target.checked = checked
    LaunchedEffect(fadeIn) {
        if (fadeIn) {
            delay(50.milliseconds)
            fadeVisible.value = true
        }
    }
    UIKitView(
        factory = {
            NativeSwitchContainerView(fadeIn)
                .apply {
                    hidden = nativeIosSwitchesSuppressed
                    val toggle =
                        UISwitch().apply {
                            translatesAutoresizingMaskIntoConstraints = false
                            onTintColor = null
                            alpha = if (fadeIn) 0.0 else 1.0
                            addTarget(target, NSSelectorFromString("valueChanged:"), UIControlEventValueChanged)
                        }
                    addSubview(toggle)
                    NSLayoutConstraint.activateConstraints(
                        listOf(
                            toggle.centerXAnchor.constraintEqualToAnchor(centerXAnchor),
                            toggle.centerYAnchor.constraintEqualToAnchor(centerYAnchor),
                        ),
                    )
                }.also(activeNativeSwitchContainers::add)
        },
        modifier =
            modifier
                .size(width = 72.dp, height = 44.dp),
        update = {
            val toggle = it.subviews.firstOrNull() as? UISwitch ?: return@UIKitView
            it.hidden = nativeIosSwitchesSuppressed
            it.clipInteropWrapper()
            it.setContentVisible(fadeVisible.value)
            if (toggle.on != checked) {
                toggle.setOn(checked, animated = true)
            }
            if (toggle.enabled != enabled) toggle.enabled = enabled
        },
        onRelease = {
            target.cancelPendingChange()
            activeNativeSwitchContainers.remove(it)
        },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative,
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = true,
            ),
    )
}

@OptIn(ExperimentalForeignApi::class)
private class NativeSwitchContainerView(
    fadeIn: Boolean,
) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    private var contentVisible = !fadeIn

    override fun didMoveToSuperview() {
        super.didMoveToSuperview()
        clipInteropWrapper()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        clipInteropWrapper()
    }

    fun setContentVisible(visible: Boolean) {
        if (contentVisible == visible) return
        contentVisible = visible
        val toggle = subviews.firstOrNull() as? UISwitch ?: return
        if (visible) {
            animateWithDuration(0.25) {
                toggle.alpha = 1.0
            }
        } else {
            toggle.alpha = 0.0
        }
    }

    fun clipInteropWrapper() {
        clipsToBounds = false
        superview?.clipsToBounds = false
        superview?.superview?.clipsToBounds = true
    }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class SwitchTarget(
    private val scope: CoroutineScope,
    var onCheckedChange: (Boolean) -> Unit,
) : NSObject() {
    var checked: Boolean = false
    private var pendingChange: Job? = null

    @ObjCAction
    fun valueChanged(sender: UISwitch) {
        val requestedValue = sender.on
        pendingChange?.cancel()
        pendingChange =
            scope.launch {
                onCheckedChange(requestedValue)
                if (sender.on != checked) {
                    sender.setOn(checked, animated = true)
                }
            }
    }

    fun cancelPendingChange() {
        pendingChange?.cancel()
    }
}
