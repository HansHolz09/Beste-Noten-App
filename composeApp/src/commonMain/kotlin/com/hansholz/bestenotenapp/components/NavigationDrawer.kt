@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationDrawer(drawerState: DrawerState, hazeState: HazeState, drawerContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    if (windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HazeModalDrawerSheet(
                    drawerState = drawerState,
                    hazeState = hazeState,
                    drawerContainerColor = Color.Transparent,
                ) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        drawerContent()
                    }
                }
            }
        ) {
            Box(Modifier.hazeSource(hazeState, 1f)) {
                content()
            }
        }
    } else {
        val density = LocalDensity.current
        var width by remember { mutableStateOf(0.dp) }
        CloseableNavigationDrawer(
            drawerState = drawerState,
            modifier = Modifier.onGloballyPositioned {
                width = with(density) { it.size.width.toDp() }
            },
            drawerContent = {
                Row {
                    PermanentDrawerSheet(
                        modifier = if (windowWidthSizeClass == WindowWidthSizeClass.MEDIUM) Modifier.width(width / 2.5f) else Modifier,
                        drawerContainerColor = Color.Transparent
                    ) {
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            drawerContent()
                        }
                    }
                    VerticalDivider(thickness = 2.dp, color = colorScheme.outline)
                }
            }
        ) {
            content()
        }
    }
}