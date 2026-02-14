@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.background
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.hansholz.bestenotenapp.components.ProvideCupertinoOverscrollEffect
import com.hansholz.bestenotenapp.components.RepeatingBackground
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.navigation.AppNavigation
import com.hansholz.bestenotenapp.theme.AppTheme
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.imageResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    isDark: (Boolean) -> Unit = {},
    colors: (ColorScheme) -> Unit = {},
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    AppTheme(colors) {
        val isDark = LocalThemeIsDark.current
        isDark(isDark)
        ProvideCupertinoOverscrollEffect(getPlatform() != Platform.DESKTOP) {
            SettingsProvider {
                val toasterState = rememberToasterState()
                val viewModel = viewModel { ViewModel(toasterState) }
                Box(Modifier.hazeSource(AppHazeState.current.value)) {
                    val blurEnabled = LocalBlurEnabled.current.value
                    val backgroundAlpha = animateFloatAsState(if (LocalBackgroundEnabled.current.value) (if (blurEnabled) 1f else 0.2f) else 0f, tween(750))
                    RepeatingBackground(
                        imageBitmap = imageResource(Res.drawable.background),
                        modifier =
                            Modifier
                                .hazeSource(AppHazeState.current.value)
                                .hazeSource(viewModel.hazeBackgroundState)
                                .hazeSource(viewModel.hazeBackgroundState1)
                                .hazeSource(viewModel.hazeBackgroundState2)
                                .hazeSource(viewModel.hazeBackgroundState3)
                                .enhancedHazeEffect()
                                .alpha(backgroundAlpha.value),
                    )

                    AppNavigation(viewModel, onNavHostReady)
                }

                Toaster(
                    state = toasterState,
                    richColors = true,
                    darkTheme = isDark,
                )
            }
        }
    }
}
