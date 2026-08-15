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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationDrawer(
    compactDrawerState: DrawerState,
    mediumExpandedDrawerState: DrawerState,
    hazeState: HazeState,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val hiddenDrawerState = remember { DrawerState(DrawerValue.Closed) }
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isCompactWindow = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isMediumWindow =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val windowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Vertical + WindowInsetsSides.Start,
        )
    LaunchedEffect(isCompactWindow) {
        compactDrawerState.close()
    }
    ModalNavigationDrawer(
        drawerState = compactDrawerState,
        drawerContent = {
            HazeModalDrawerSheet(
                drawerState = compactDrawerState,
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
        gesturesEnabled = isCompactWindow,
    ) {
        BoxWithConstraints {
            val drawerSheetModifier =
                if (isMediumWindow) {
                    Modifier.width(maxWidth / 2.5f)
                } else {
                    Modifier
                }
            CloseableNavigationDrawer(
                drawerState = if (isCompactWindow) hiddenDrawerState else mediumExpandedDrawerState,
                drawerContent = {
                    if (!isCompactWindow) {
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
                    }
                },
            ) {
                Box(
                    Modifier.then(
                        if (isCompactWindow) Modifier.hazeSource(hazeState, 1f) else Modifier,
                    ),
                ) {
                    content()
                }
            }
        }
    }
}
