package com.hansholz.bestenotenapp

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.LocalBiometricAuthenticationAvailable
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.navigation.Fragment
import com.hansholz.bestenotenapp.utils.FileKitWindowObject
import dev.hansholz.advancedmenubar.DefaultMacMenuBar
import dev.hansholz.advancedmenubar.NativeTextContextMenuProvider
import dev.nucleusframework.aot.runtime.AotRuntime
import dev.nucleusframework.core.runtime.NucleusApp
import dev.nucleusframework.core.runtime.SingleInstanceManager
import dev.nucleusframework.graalvm.GraalVmInitializer
import dev.nucleusframework.window.DecoratedWindowDefaults
import dev.nucleusframework.window.TitleBarPlacement
import dev.nucleusframework.window.WindowControls
import dev.nucleusframework.window.WindowScaffold
import dev.nucleusframework.window.styling.LocalTitleBarStyle
import dev.nucleusframework.window.tao.DecoratedWindow
import dev.nucleusframework.window.tao.MacOSStyle
import dev.nucleusframework.window.tao.taoApplication
import dev.nucleusframework.window.windowDragArea
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostOs
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class)
fun main() {
    GraalVmInitializer.initialize()
    System.setProperty("ksafe.jvm.keyVault", "software") // because no signed macOS app
    FileKit.init(NucleusApp.appId)
    taoApplication {
        val backNavigationInput =
            remember {
                object : NavigationEventInput() {
                    fun onKeyEvent(event: KeyEvent): Boolean {
                        if (event.type != KeyEventType.KeyDown || event.key != Key.Escape) return false
                        dispatchOnBackCompleted()
                        return true
                    }
                }
            }
        DecoratedWindow(
            onCloseRequest = ::exitApplication,
            onKeyEvent = backNavigationInput::onKeyEvent,
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1200.dp, 800.dp)),
            title = "Beste-Noten-App",
            icon = if (hostOs.isMacOS) null else painterResource(Res.drawable.logo),
            minimumSize = DpSize(700.dp, 500.dp),
            macOSStyle = MacOSStyle.Modern,
        ) {
            val navigationEventDispatcher = LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher
            DisposableEffect(navigationEventDispatcher, backNavigationInput) {
                navigationEventDispatcher?.addInput(backNavigationInput)
                onDispose { navigationEventDispatcher?.removeInput(backNavigationInput) }
            }

            val isSingle = SingleInstanceManager.isSingleInstance { window.focus() }
            if (!isSingle) exitProcess(0)

            if (AotRuntime.isTraining()) {
                LaunchedEffect(Unit) {
                    delay(10.seconds)
                    exitProcess(0)
                }
            }

            LaunchedEffect(Unit) {
                if (hostOs.isWindows) {
                    FileKitWindowObject.setHandle(window.nativeHandle)
                }
            }

            val scope = rememberCoroutineScope()
            var navController by remember { mutableStateOf<NavController?>(null) }
            val uriHandler = LocalUriHandler.current
            DefaultMacMenuBar(
                appName = NucleusApp.appId,
                onSettingsClick = {
                    scope.launch {
                        navController?.navigate(Fragment.Settings.route)
                    }
                },
                onHelpClick = {
                    uriHandler.openUri("https://github.com/HansHolz09/Beste-Noten-App")
                },
            )

            val titleBarHeight = remember { mutableStateOf(64.dp) }
            var isDark by remember { mutableStateOf(false) }
            WindowScaffold(
                titleBar = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(titleBarHeight.value)
                            .windowDragArea(),
                    ) {
                        CompositionLocalProvider(
                            LocalTitleBarStyle provides if (isDark) DecoratedWindowDefaults.darkTitleBarStyle() else DecoratedWindowDefaults.lightTitleBarStyle(),
                        ) {
                            this@DecoratedWindow.WindowControls(Modifier.align(Alignment.CenterEnd))
                        }
                    }
                },
                titleBarPlacement = TitleBarPlacement.Overlay(false, true),
            ) {
                CompositionLocalProvider(
                    LocalContextMenuRepresentation provides if (isDark) DarkDefaultContextMenuRepresentation else LightDefaultContextMenuRepresentation,
                    LocalNavigationDrawerTopPadding provides
                        if (getExactPlatform() == ExactPlatform.MACOS && !window.isFullscreen) titleBarHeight.value else null,
                    LocalBiometricAuthenticationAvailable provides runBlocking { KSafeBiometrics.biometricsAvailable() },
                ) {
                    NativeTextContextMenuProvider(isDark) {
                        App(
                            isDark = { isDark = it },
                            onNavHostReady = {
                                navController = it
                            },
                        )
                    }
                }
            }
        }
    }
}
