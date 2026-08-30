@file:OptIn(ExperimentalComposeUiApi::class)

package com.hansholz.bestenotenapp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.hansholz.bestenotenapp.components.NativeAppearanceSelector
import com.hansholz.bestenotenapp.components.NativeDatePicker
import com.hansholz.bestenotenapp.components.NativeDialogBackdrop
import com.hansholz.bestenotenapp.components.NativePrimaryTabRow
import com.hansholz.bestenotenapp.components.NativeSwitch
import com.hansholz.bestenotenapp.components.NativeTimePicker
import com.hansholz.bestenotenapp.components.hideNativeSwitches
import com.hansholz.bestenotenapp.components.hideVisibleNativeDateTimePickers
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.LocalBiometricAuthenticationAvailable
import com.hansholz.bestenotenapp.main.LocalGlobalEasterEgg
import com.hansholz.bestenotenapp.main.LocalHideNativeDateTimePickers
import com.hansholz.bestenotenapp.main.LocalHideNativeInterop
import com.hansholz.bestenotenapp.main.LocalNativeAppearanceSelector
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativeContentTopPadding
import com.hansholz.bestenotenapp.main.LocalNativeDatePicker
import com.hansholz.bestenotenapp.main.LocalNativeDialogBackdrop
import com.hansholz.bestenotenapp.main.LocalNativePrimaryTabRow
import com.hansholz.bestenotenapp.main.LocalNativeSwitch
import com.hansholz.bestenotenapp.main.LocalNativeTimePicker
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.notifications.ensureIosNotificationsInitialized
import com.hansholz.bestenotenapp.theme.LocalNativeSystemIsDark
import com.hansholz.bestenotenapp.utils.installLegacyInsetsPatch
import com.hansholz.bestenotenapp.utils.isInWindowMode
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import kotlinx.coroutines.runBlocking
import platform.UIKit.UIColor
import platform.UIKit.UIViewController

fun mainViewController(nativeBridge: NativeComponentBridge? = null): UIViewController {
    val controller =
        ComposeUIViewController(configure = { opaque = nativeBridge == null }) {
            PlatformImeOptions { usingNativeTextInput(true) }
            ensureIosNotificationsInitialized()
            val nativeComponentsEnabled = nativeBridge?.enabledState ?: remember { mutableStateOf(false) }
            val biometricAuthenticationAvailable = remember { runBlocking { KSafeBiometrics.biometricsAvailable() } }
            CompositionLocalProvider(
                LocalNavigationDrawerTopPadding provides if (isInWindowMode()) 50.dp else null,
                LocalBiometricAuthenticationAvailable provides biometricAuthenticationAvailable,
                LocalNativeComponentsEnabled provides nativeComponentsEnabled,
                LocalNativeSystemIsDark provides nativeBridge?.systemIsDarkState?.value,
                LocalNativeContentTopPadding provides nativeBridge?.contentTopInsetState?.value?.dp,
                LocalNativeDialogBackdrop provides { modifier, glassFrames ->
                    NativeDialogBackdrop(modifier, glassFrames)
                },
                LocalNativeDatePicker provides { selected, onSelected, modifier ->
                    NativeDatePicker(selected, onSelected, modifier)
                },
                LocalNativeTimePicker provides { hour, minute, onTimeChanged, modifier ->
                    NativeTimePicker(hour, minute, onTimeChanged, modifier)
                },
                LocalHideNativeDateTimePickers provides { hideVisibleNativeDateTimePickers() },
                LocalNativePrimaryTabRow provides
                    nativeBridge?.let { bridge ->
                        { labels, selected, onSelected, modifier ->
                            NativePrimaryTabRow(bridge, labels, selected, onSelected, modifier)
                        }
                    },
                LocalGlobalEasterEgg provides { type -> nativeBridge?.showEasterEgg(type) },
                LocalHideNativeInterop provides { hideNativeSwitches() },
                LocalNativeSwitch provides { checked, onCheckedChange, enabled, modifier, fadeIn ->
                    NativeSwitch(checked, onCheckedChange, enabled, modifier, fadeIn)
                },
                LocalNativeAppearanceSelector provides { selected, onSelected, modifier ->
                    NativeAppearanceSelector(selected, onSelected, modifier)
                },
            ) {
                App(
                    theme = { isDark, usesSystemAppearance ->
                        nativeBridge?.updateTheme(isDark, usesSystemAppearance)
                    },
                    colors = { colors -> nativeBridge?.updateColorScheme(colors) },
                    onNavHostReady = { controller -> nativeBridge?.attach(controller) },
                    onRootDestinationChanged = { route -> nativeBridge?.rootDestinationChanged(route) },
                    onFragmentDestinationChanged = { route -> nativeBridge?.fragmentDestinationChanged(route) },
                    onCanNavigateBackChanged = { canNavigateBack -> nativeBridge?.canNavigateBackChanged(canNavigateBack) },
                )
            }
        }
    if (nativeBridge != null) {
        controller.view.backgroundColor = UIColor.clearColor
        controller.view.opaque = false
    }
    installLegacyInsetsPatch(controller.view)
    return controller
}
