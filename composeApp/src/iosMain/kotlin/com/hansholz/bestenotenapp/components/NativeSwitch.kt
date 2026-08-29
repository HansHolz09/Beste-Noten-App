package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UISwitch
import platform.UIKit.UIView
import platform.darwin.NSObject

private val activeNativeSwitchContainers = mutableSetOf<UIView>()
private var nativeIosSwitchesSuppressed = false

internal fun hideNativeSwitches() {
    nativeIosSwitchesSuppressed = true
    activeNativeSwitchContainers.forEach { it.hidden = true }
    hideNativeAppearanceButtons()
}

internal fun showNativeSwitches() {
    nativeIosSwitchesSuppressed = false
    showNativeAppearanceButtons()
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    val target = remember { SwitchTarget(scope, onCheckedChange) }
    target.onCheckedChange = onCheckedChange
    target.checked = checked
    UIKitView(
        factory = {
            NativeSwitchContainerView()
                .apply {
                    hidden = nativeIosSwitchesSuppressed
                    val toggle =
                        UISwitch().apply {
                            translatesAutoresizingMaskIntoConstraints = false
                            onTintColor = null
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
private class NativeSwitchContainerView : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    override fun didMoveToSuperview() {
        super.didMoveToSuperview()
        clipInteropWrapper()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        clipInteropWrapper()
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
