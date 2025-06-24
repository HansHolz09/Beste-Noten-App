@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.background
import com.hansholz.bestenotenapp.components.*
import com.hansholz.bestenotenapp.navigation.AppNavigation
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.theme.AppTheme
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalIsDark
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import com.hansholz.bestenotenapp.utils.forceHitTest
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.presets.attentionseekers.Jello
import com.nomanr.animate.compose.presets.attentionseekers.RubberBand
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(isDark: (Boolean) -> Unit = {}, colors: (ColorScheme) -> Unit = {}) {
    AppTheme {
        colors(colorScheme)
        isDark(LocalIsDark.current.value)
        ProvideCupertinoOverscrollEffect(listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform())) {
            SettingsProvider {
                val scope = rememberCoroutineScope()
                val viewModel = viewModel { ViewModel() }
                val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
                val blurEnabled = LocalBlurEnabled.current.value

                val backgroundAlpha = animateFloatAsState(if (LocalBackgroundEnabled.current.value) (if (blurEnabled) 1f else 0.2f) else 0f, tween(750))
                RepeatingBackground(
                    imageBitmap = imageResource(Res.drawable.background),
                    modifier = Modifier
                        .hazeSource(viewModel.hazeBackgroundState)
                        .hazeSource(viewModel.hazeBackgroundState1)
                        .hazeSource(viewModel.hazeBackgroundState2)
                        .hazeSource(viewModel.hazeBackgroundState3)
                        .enhancedHazeEffect()
                        .alpha(backgroundAlpha.value)
                )

                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()
                NavigationDrawer(
                    drawerState = if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.compactDrawerState.value else viewModel.mediumExpandedDrawerState.value,
                    hazeState = viewModel.hazeBackgroundState,
                    drawerContent = {
                        Column {
                            Spacer(Modifier.fillMaxWidth().height(LocalMacOSTitelBarHeight.current ?: 15.dp).customTitleBarMouseEventHandler { forceHitTest(it) })
                            val animateState = rememberAnimatedState()
                            LaunchedEffect(viewModel.compactDrawerState.value.currentValue) {
                                while (viewModel.compactDrawerState.value.isOpen) {
                                    animateState.animate()
                                    delay(5000)
                                }
                            }
                            EnhancedAnimated(
                                preset = RubberBand(0.05f),
                                durationMillis = 1000,
                                state = animateState
                            ) {
                                Text(
                                    text = "Beste-Noten-App",
                                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 40.dp),
                                    color = colorScheme.onSurface,
                                    autoSize = TextAutoSize.StepBased(),
                                    fontFamily = FontFamily.Serif,
                                    maxLines = 1
                                )
                            }
                            EnhancedAnimated(
                                preset = Jello(),
                                durationMillis = 1000,
                                state = animateState
                            ) {
                                Text(
                                    text = "für beste.schule",
                                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 80.dp),
                                    color = colorScheme.onSurface,
                                    autoSize = TextAutoSize.StepBased(5.sp),
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                            Spacer(Modifier.height(15.dp))
                            Screen.entries.forEach { screen ->
                                AnimatedContent(
                                    targetState = currentRoute?.destination?.route == screen.route,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(500))
                                            .togetherWith(fadeOut(animationSpec = tween(500)))
                                    },
                                ) { isCurrentScreen ->
                                    NavigationDrawerItem(
                                        label = { Text(screen.label) },
                                        selected = isCurrentScreen,
                                        onClick = {
                                            scope.launch {
                                                navController.navigate(screen.route)
                                                if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.closeOrOpenDrawer(windowWithSizeClass)
                                            }
                                        },
                                        modifier = Modifier.padding(10.dp).then(
                                            if (isCurrentScreen) Modifier.border(2.dp, colorScheme.onSurface, shapes.extraExtraLarge) else Modifier
                                        ),
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = colorScheme.secondaryContainer.copy(0.7f),
                                            unselectedTextColor = colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                            AnimatedContent(
                                targetState = currentRoute?.destination?.route == Screen.Settings.route,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(500))
                                        .togetherWith(fadeOut(animationSpec = tween(500)))
                                },
                            ) { isCurrentScreen ->
                                NavigationDrawerItem(
                                    label = { Text("Einstellungen") },
                                    selected = isCurrentScreen,
                                    onClick = {
                                        scope.launch {
                                            navController.navigate(Screen.Settings.route)
                                            if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.closeOrOpenDrawer(windowWithSizeClass)
                                        }
                                    },
                                    modifier = Modifier.padding(10.dp).then(
                                        if (isCurrentScreen) Modifier.border(2.dp, colorScheme.onSurface, shapes.extraExtraLarge) else Modifier
                                    ),
                                    icon = { Icon(Icons.Outlined.Settings, null) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = colorScheme.secondaryContainer.copy(0.7f),
                                        unselectedTextColor = colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                ) {
                    AppNavigation(viewModel, navController)
                }
            }
        }
    }
}
