@file:OptIn(ExperimentalComposeUiApi::class)

package com.hansholz.bestenotenapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.navigation.AppDrawerContent
import platform.UIKit.UIColor
import platform.UIKit.UIViewController
import top.ltfan.multihaptic.compose.rememberVibrator

fun sidebarViewController(nativeBridge: NativeComponentBridge): UIViewController =
    ComposeUIViewController(configure = { opaque = false }) {
        NativeTheme(nativeBridge) {
            SidebarContent(nativeBridge)
        }
    }.also { controller ->
        controller.view.backgroundColor = UIColor.clearColor
        controller.view.opaque = false
    }

@Composable
private fun SidebarContent(nativeBridge: NativeComponentBridge) {
    val vibrator = rememberVibrator()
    Box(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        AppDrawerContent(
            selectedRoute = nativeBridge.selectedFragmentState.value,
            topPadding = nativeBridge.sidebarTopInsetState.value.dp,
            animateLogo = false,
            onDestinationSelected = {
                nativeBridge.selectFragment(it.route)
                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
            },
            onLogoClick = {
                nativeBridge.showEasterEgg("logo")
                vibrator.enhancedVibrateN(EnhancedVibrations.LOGO_RAIN)
            },
        )
    }
}
