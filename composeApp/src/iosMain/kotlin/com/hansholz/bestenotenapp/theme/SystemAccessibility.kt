package com.hansholz.bestenotenapp.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityButtonShapesEnabled
import platform.UIKit.UIAccessibilityButtonShapesEnabledStatusDidChangeNotification
import platform.UIKit.UIAccessibilityDarkerSystemColorsEnabled
import platform.UIKit.UIAccessibilityDarkerSystemColorsStatusDidChangeNotification
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityIsReduceTransparencyEnabled
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification
import platform.UIKit.UIAccessibilityReduceTransparencyStatusDidChangeNotification

@Composable
internal fun rememberSystemAccessibility(): SystemAccessibility {
    var systemAccessibility by remember { mutableStateOf(currentSystemAccessibility()) }
    DisposableEffect(Unit) {
        val notificationCenter = NSNotificationCenter.defaultCenter
        val observers =
            listOf(
                UIAccessibilityReduceMotionStatusDidChangeNotification,
                UIAccessibilityDarkerSystemColorsStatusDidChangeNotification,
                UIAccessibilityReduceTransparencyStatusDidChangeNotification,
                UIAccessibilityButtonShapesEnabledStatusDidChangeNotification,
            ).map { name ->
                notificationCenter.addObserverForName(name, null, NSOperationQueue.mainQueue) {
                    systemAccessibility = currentSystemAccessibility()
                }
            }
        onDispose { observers.forEach(notificationCenter::removeObserver) }
    }
    return systemAccessibility
}

private fun currentSystemAccessibility() =
    SystemAccessibility(
        reduceMotion = UIAccessibilityIsReduceMotionEnabled(),
        increaseContrast = UIAccessibilityDarkerSystemColorsEnabled(),
        reduceTransparency = UIAccessibilityIsReduceTransparencyEnabled(),
        showBorders = UIAccessibilityButtonShapesEnabled(),
    )
