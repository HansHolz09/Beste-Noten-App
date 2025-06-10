@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.background
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.NavigationDrawer
import com.hansholz.bestenotenapp.components.ProvideCupertinoOverscrollEffect
import com.hansholz.bestenotenapp.components.RepeatingBackground
import com.hansholz.bestenotenapp.screens.*
import com.hansholz.bestenotenapp.theme.AppTheme
import com.nomanr.animate.compose.animated.rememberAnimatedState
import com.nomanr.animate.compose.presets.attentionseekers.Jello
import com.nomanr.animate.compose.presets.attentionseekers.RubberBand
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    AppTheme {
        ProvideCupertinoOverscrollEffect(listOf(Platform.ANDROID, Platform.IOS).contains(getPlatform())) {
            val scope = rememberCoroutineScope()
            val viewModel = viewModel { ViewModel() }
            val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

            RepeatingBackground(
                imageBitmap = imageResource(Res.drawable.background),
                modifier = Modifier
                    .hazeSource(viewModel.hazeBackgroundState)
                    .hazeSource(viewModel.hazeBackgroundState2)
                    .hazeSource(viewModel.hazeBackgroundState3)
                    .hazeEffect()
            )
            NavigationDrawer(
                drawerState = if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.compactDrawerState.value else viewModel.mediumExpandedDrawerState.value,
                hazeState = viewModel.hazeBackgroundState,
                drawerContent = {
                    Column {
                        Spacer(Modifier.height(15.dp))
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
                        Screen.entries.filter { it != Screen.SETTINGS }.forEach {
                            NavigationDrawerItem(
                                label = { Text(it.label) },
                                selected = viewModel.currentScreen.value == it,
                                onClick = {
                                    scope.launch {
                                        viewModel.currentScreen.value = it
                                        if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.closeOrOpenDrawer(windowWithSizeClass)
                                    }
                                },
                                modifier = Modifier.padding(10.dp).then(
                                    if (viewModel.currentScreen.value == it) Modifier.border(2.dp, colorScheme.onSurface, shapes.extraExtraLarge) else Modifier
                                ),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = colorScheme.secondaryContainer.copy(0.7f),
                                    unselectedTextColor = colorScheme.onSurface
                                )
                            )
                        }
                        HorizontalDivider(thickness = 2.dp)
                        NavigationDrawerItem(
                            label = { Text("Einstellungen") },
                            selected = viewModel.currentScreen.value == Screen.SETTINGS,
                            onClick = {
                                scope.launch {
                                    viewModel.currentScreen.value = Screen.SETTINGS
                                    if (windowWithSizeClass == WindowWidthSizeClass.COMPACT) viewModel.closeOrOpenDrawer(windowWithSizeClass)
                                }
                            },
                            modifier = Modifier.padding(10.dp).then(
                                if (viewModel.currentScreen.value == Screen.SETTINGS) Modifier.border(2.dp, colorScheme.onSurface, shapes.extraExtraLarge) else Modifier
                            ),
                            icon = { Icon(Icons.Outlined.Settings, null) },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = colorScheme.secondaryContainer.copy(0.7f),
                                unselectedTextColor = colorScheme.onSurface
                            )
                        )
                    }
                }
            ) {
                AnimatedContent(
                    targetState = viewModel.currentScreen.value,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500))
                            .togetherWith(fadeOut(animationSpec = tween(500)))
                    }
                ) {
                    when(it) {
                        Screen.HOME -> Home(viewModel)
                        Screen.GRADES -> Grades(viewModel)
                        Screen.SUBJECTS_AND_TEACHERS -> SubjectsAndTeachers(viewModel)
                        Screen.STATS -> Stats()
                        Screen.SETTINGS -> Settings(viewModel)
                    }
                }
            }
        }
    }
}

enum class Screen(val label: String) {
    HOME("Startseite"),
    GRADES("Noten"),
    SUBJECTS_AND_TEACHERS("Fächer und Lehrer"),
    STATS("Statistiken"),
    SETTINGS("Einstellungen")
}
