package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectInset
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIBezierPath
import platform.UIKit.UIButton
import platform.UIKit.UIButtonConfiguration
import platform.UIKit.UIButtonConfigurationCornerStyleCapsule
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UICornerConfiguration
import platform.UIKit.UIGlassContainerEffect
import platform.UIKit.UIGlassEffect
import platform.UIKit.UIGlassEffectStyle
import platform.UIKit.UIImage
import platform.UIKit.UIImageRenderingMode
import platform.UIKit.UIPointerEffect
import platform.UIKit.UIPointerShape
import platform.UIKit.UIPointerStyle
import platform.UIKit.UIPreviewParameters
import platform.UIKit.UITargetedPreview
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIVisualEffectView
import platform.darwin.NSObject

private val appearanceSelectors = mutableSetOf<AppearanceSelectorView>()
private var nativeIosAppearanceSelectorsSuppressed = false

internal fun hideNativeAppearanceButtons() {
    nativeIosAppearanceSelectorsSuppressed = true
    appearanceSelectors.forEach { it.hidden = true }
}

internal fun showNativeAppearanceButtons() {
    nativeIosAppearanceSelectorsSuppressed = false
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NativeAppearanceSelector(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier,
) {
    val target = remember { AppearanceSelectorTarget(onSelected) }
    target.onSelected = onSelected
    UIKitView(
        factory = {
            AppearanceSelectorView(target).also {
                it.hidden = nativeIosAppearanceSelectorsSuppressed
                appearanceSelectors += it
            }
        },
        modifier = modifier.size(width = 142.dp, height = 44.dp),
        update = { selector ->
            selector.updateSelection(selectedIndex)
            selector.hidden = nativeIosAppearanceSelectorsSuppressed
        },
        onRelease = { appearanceSelectors -= it },
        properties =
            UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.Cooperative(),
                isNativeAccessibilityEnabled = true,
                placedAsOverlay = true,
            ),
    )
}

@OptIn(ExperimentalForeignApi::class)
private class AppearanceSelectorView(
    target: AppearanceSelectorTarget,
) : UIView(frame = CGRectMake(0.0, 0.0, 142.0, 44.0)) {
    private val glassViews = mutableListOf<UIVisualEffectView>()
    private val buttons = mutableListOf<UIButton>()
    private val containerEffect = UIGlassContainerEffect().apply { setSpacing(4.0) }
    private val container = UIVisualEffectView(effect = containerEffect)
    private var currentSelection = -1

    init {
        container.setFrame(bounds)
        container.autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        addSubview(container)

        val imageNames = listOf("material-brightness-auto", "material-light-mode", "material-dark-mode")
        imageNames.forEachIndexed { index, imageName ->
            val glass = makeGlassView(selected = false)
            glass.setFrame(CGRectMake(1.0 + index * 50.0, 2.0, 40.0, 40.0))
            glass.setCornerConfiguration(UICornerConfiguration.capsuleConfiguration())
            container.contentView.addSubview(glass)
            glassViews += glass

            val button = UIButton.buttonWithType(UIButtonTypeSystem)
            button.setFrame(glass.bounds)
            button.autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
            button.layer.cornerRadius = 20.0
            button.clipsToBounds = true
            button.setTag(index.toLong())
            button.setPointerInteractionEnabled(true)
            button.setPointerStyleProvider { btn, _, _ ->
                val boundsRect = btn!!.bounds
                val inset = 4.0
                val insetRect = CGRectInset(boundsRect, inset, inset)
                val radius = insetRect.useContents { size.height } / 2.0
                val path =
                    UIBezierPath.bezierPathWithRoundedRect(
                        rect = insetRect,
                        cornerRadius = radius,
                    )
                val params =
                    UIPreviewParameters().apply {
                        visiblePath = path
                    }
                val preview = UITargetedPreview(view = btn, parameters = params)
                val effect = UIPointerEffect.effectWithPreview(preview)
                val shape = UIPointerShape.shapeWithRoundedRect(insetRect, radius)
                UIPointerStyle.styleWithEffect(effect, shape = shape)
            }
            button.configuration =
                UIButtonConfiguration.plainButtonConfiguration().apply {
                    cornerStyle = UIButtonConfigurationCornerStyleCapsule
                    image =
                        UIImage
                            .imageNamed(imageName)
                            ?.imageWithRenderingMode(UIImageRenderingMode.UIImageRenderingModeAlwaysTemplate)
                }
            button.addTarget(target, NSSelectorFromString("selected:"), UIControlEventTouchUpInside)
            glass.contentView.addSubview(button)
            buttons += button
        }
    }

    fun updateSelection(selectedIndex: Int) {
        if (selectedIndex == currentSelection) return
        currentSelection = selectedIndex
        glassViews.forEachIndexed { index, view ->
            view.setEffect(makeGlassEffect(index == selectedIndex))
            val foregroundColor = if (index == selectedIndex) UIColor.blackColor else null
            buttons[index].setTintColor(foregroundColor)
            buttons[index].configuration =
                buttons[index].configuration?.apply {
                    baseForegroundColor = foregroundColor
                }
        }
    }

    override fun didMoveToWindow() {
        super.didMoveToWindow()
        val selection = currentSelection
        currentSelection = -1
        updateSelection(selection)
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        val selection = currentSelection
        currentSelection = -1
        updateSelection(selection)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        container.setFrame(bounds)
    }

    private fun makeGlassView(selected: Boolean): UIVisualEffectView = UIVisualEffectView(effect = makeGlassEffect(selected))

    private fun makeGlassEffect(selected: Boolean): UIGlassEffect =
        UIGlassEffect.effectWithStyle(UIGlassEffectStyle.UIGlassEffectStyleRegular).apply {
            setInteractive(true)
            setTintColor(if (selected) tintColor ?: UIColor.yellowColor else null)
        }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private class AppearanceSelectorTarget(
    var onSelected: (Int) -> Unit,
) : NSObject() {
    @ObjCAction
    fun selected(sender: UIButton) {
        onSelected(sender.tag.toInt())
    }
}
