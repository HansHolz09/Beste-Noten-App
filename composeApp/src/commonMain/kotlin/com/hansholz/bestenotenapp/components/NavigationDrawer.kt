package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
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
    val currentContent by rememberUpdatedState(content)
    val movableContent = remember { movableContentOf { currentContent() } }
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isCompactWindow = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isMediumWindow =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val windowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Vertical + WindowInsetsSides.Start,
        )
    if (isCompactWindow) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                HazeModalDrawerSheet(
                    drawerState = drawerState,
                    hazeState = hazeState,
                    drawerContainerColor = Color.Transparent,
                    windowInsets = WindowInsets(),
                ) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .windowInsetsPadding(windowInsets),
                    ) {
                        drawerContent()
                    }
                }
            },
        ) {
            Box(Modifier.hazeSource(hazeState, 1f)) {
                movableContent()
            }
        }
    } else {
        BoxWithConstraints {
            val drawerSheetModifier =
                if (isMediumWindow) {
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
                            windowInsets = WindowInsets(),
                        ) {
                            Column(
                                Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .windowInsetsPadding(windowInsets),
                            ) {
                                drawerContent()
                            }
                        }
                        VerticalDivider(thickness = 2.dp, color = colorScheme.outline)
                    }
                },
            ) {
                movableContent()
            }
        }
    }
}
