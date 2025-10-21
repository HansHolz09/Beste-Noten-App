package com.hansholz.bestenotenapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.navigation.NavController
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import com.hansholz.bestenotenapp.decoratedWindow.DecoratedWindow
import com.hansholz.bestenotenapp.decoratedWindow.LocalDecoratedWindowScope
import com.hansholz.bestenotenapp.decoratedWindow.TitleBar
import com.hansholz.bestenotenapp.main.App
import com.hansholz.bestenotenapp.main.ExactPlatform
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.getExactPlatform
import com.hansholz.bestenotenapp.navigation.Fragment
import com.jetbrains.JBR
import dev.hansholz.advancedmenubar.DefaultMacMenu
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skiko.hostOs
import java.awt.Color
import java.awt.Dimension

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class, ExperimentalMaterial3Api::class)
fun main() {
    application {
        DecoratedWindow(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1200.dp, 800.dp)),
            title = "Beste-Noten-App",
            icon = painterResource(Res.drawable.logo),
        ) {
            val scope = rememberCoroutineScope()
            var navController by remember { mutableStateOf<NavController?>(null) }
            val uriHandler = LocalUriHandler.current
            DefaultMacMenu(
                onSettingsClick = {
                    scope.launch {
                        navController?.navigate(Fragment.Settings.route)
                    }
                },
                onHelpClick = {
                    uriHandler.openUri("https://github.com/HansHolz09/Beste-Noten-App")
                },
            )
            val density = LocalDensity.current
            window.minimumSize = Dimension(with(density) { 700.toDp().roundToPx() }, with(density) { 500.toDp().roundToPx() })
            val titleBarHeight = remember { mutableStateOf(20.dp) }
            var isDark by remember { mutableStateOf(false) }
            CompositionLocalProvider(
                LocalTitleBarModifier provides Modifier.onGloballyPositioned { titleBarHeight.value = with(density) { it.size.height.toDp() } },
                LocalNavigationDrawerTopPadding provides
                    if (getExactPlatform() == ExactPlatform.MACOS && !LocalDecoratedWindowScope.current.state.isFullscreen) titleBarHeight.value else null,
            ) {
                App(
                    isDark = { isDark = it },
                    colors = {
                        if (JBR.isRoundedCornersManagerSupported()) {
                            JBR.getRoundedCornersManager().setRoundedCorners(
                                window,
                                if (hostOs.isWindows) {
                                    "full"
                                } else if (hostOs.isMacOS) {
                                    arrayOf(20f, 2, Color(it.outline.toArgb()))
                                } else {
                                    null
                                },
                            )
                        }
                    },
                    onNavHostReady = {
                        navController = it
                    },
                )
            }
            TitleBar(isDark = isDark, titleBarHeight = titleBarHeight)
        }
    }
}
