@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    hazeState: HazeState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
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
            },
        ) {
            Box(Modifier.hazeSource(hazeState, 1f)) {
                content()
            }
        }
    } else {
        BoxWithConstraints {
            val drawerSheetModifier =
                if (windowWidthSizeClass == WindowWidthSizeClass.MEDIUM) {
                    Modifier.width(maxWidth / 2.5f)
                } else {
                    Modifier
                }
            CloseableNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Row {
                        PermanentDrawerSheet(
                            modifier = drawerSheetModifier,
                            drawerContainerColor = Color.Transparent,
                        ) {
                            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                                drawerContent()
                            }
                        }
                        VerticalDivider(thickness = 2.dp, color = colorScheme.outline)
                    }
                },
            ) {
                content()
            }
        }
    }
}
