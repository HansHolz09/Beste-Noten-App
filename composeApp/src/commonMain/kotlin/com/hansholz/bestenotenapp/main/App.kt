@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Airport_shuttle
import com.composables.icons.materialsymbols.rounded.Backpack
import com.composables.icons.materialsymbols.rounded.Book_2
import com.composables.icons.materialsymbols.rounded.Dictionary
import com.composables.icons.materialsymbols.rounded.Directions_bus
import com.composables.icons.materialsymbols.rounded.Directions_car
import com.composables.icons.materialsymbols.rounded.Diversity_2
import com.composables.icons.materialsymbols.rounded.Diversity_3
import com.composables.icons.materialsymbols.rounded.Ink_pen
import com.composables.icons.materialsymbols.rounded.Jamboard_kiosk
import com.composables.icons.materialsymbols.rounded.Nest_clock_farsight_analog
import com.composables.icons.materialsymbols.rounded.Pinboard
import com.composables.icons.materialsymbols.rounded.School
import com.composables.icons.materialsymbols.rounded.Stylus
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.hansholz.bestenotenapp.components.ProvideCupertinoOverscrollEffect
import com.hansholz.bestenotenapp.components.ScatterConfig
import com.hansholz.bestenotenapp.components.ScatterItem
import com.hansholz.bestenotenapp.components.ScatteredIconBackground
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.navigation.AppNavigation
import com.hansholz.bestenotenapp.theme.AppTheme
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import dev.chrisbanes.haze.hazeSource

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

                val blurEnabled = LocalBlurEnabled.current.value
                val targetBackgroundAlpha = if (LocalBackgroundEnabled.current.value) (if (blurEnabled) 0.5f else 0.15f) else 0f
                val backgroundAlpha by animateFloatAsState(targetBackgroundAlpha, tween(750))
                if (targetBackgroundAlpha > 0f || backgroundAlpha > 0.01f) {
                    ScatteredIconBackground(
                        items =
                            remember {
                                listOf(
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.School),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Nest_clock_farsight_analog),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Jamboard_kiosk),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Pinboard),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Stylus),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Ink_pen),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Dictionary),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Book_2),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Backpack),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Diversity_2),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Diversity_3),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Directions_bus),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Directions_car),
                                    ScatterItem.IconItem(MaterialSymbols.Rounded.Airport_shuttle),
                                )
                            },
                        modifier =
                            Modifier
                                .hazeSource(viewModel.hazeBackgroundState)
                                .hazeSource(viewModel.hazeBackgroundState1)
                                .hazeSource(viewModel.hazeBackgroundState2)
                                .hazeSource(viewModel.hazeBackgroundState3)
                                .enhancedHazeEffect(),
                        config =
                            ScatterConfig(
                                cellSize = 100.dp,
                                itemSizeFraction = 0.7f,
                            ),
                        alpha = backgroundAlpha,
                    )
                }

                AppNavigation(viewModel, onNavHostReady)

                Toaster(
                    state = toasterState,
                    richColors = true,
                    darkTheme = isDark,
                )
            }
        }
    }
}
