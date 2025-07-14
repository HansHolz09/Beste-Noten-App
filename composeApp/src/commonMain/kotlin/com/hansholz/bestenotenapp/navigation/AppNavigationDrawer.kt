@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.hansholz.bestenotenapp.components.NavigationDrawer
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimated
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.presets.attentionseekers.Jello
import com.nomanr.animate.compose.presets.attentionseekers.RubberBand
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavigationDrawer(
    viewModel: ViewModel,
    onNavHostReady: suspend (NavController) -> Unit = {},
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    NavigationDrawer(
        drawerState = if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.compactDrawerState.value else viewModel.mediumExpandedDrawerState.value,
        hazeState = viewModel.hazeBackgroundState,
        drawerContent = {
            Column(Modifier.customTitleBarMouseEventHandler()) {
                Spacer(Modifier.fillMaxWidth().height(LocalNavigationDrawerTopPadding.current ?: 15.dp))
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
                        autoSize = TextAutoSize.StepBased(10.sp),
                        fontFamily = FontFamilies.KeaniaOne(),
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
                        fontFamily = FontFamilies.Schoolbell(),
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(15.dp))
                Fragment.entries.forEach { screen ->
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
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                    targetState = currentRoute?.destination?.route == Fragment.Settings.route,
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
                                navController.navigate(Fragment.Settings.route)
                                if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.closeOrOpenDrawer(windowWithSizeClass)
                            }
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
        FragmentNavigation(
            viewModel = viewModel,
            navController = navController,
            onNavigateToLogin = onNavigateToLogin
        )
    }
    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }
}