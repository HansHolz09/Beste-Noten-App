package com.hansholz.bestenotenapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Cloud_off
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativeContentTopPadding
import com.hansholz.bestenotenapp.main.LocalUsingOfflineCache
import com.hansholz.bestenotenapp.utils.topAppBarEndPadding
import com.hansholz.bestenotenapp.utils.topAppBarStartPadding
import dev.chrisbanes.haze.HazeState

@Composable
fun TopAppBarScaffold(
    modifier: Modifier = Modifier,
    title: String,
    titleModifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    sideMenuExpanded: Boolean? = null,
    hazeState: HazeState? = null,
    content: @Composable (
    (
        innerPadding: PaddingValues,
        topAppBarBackground: @Composable (height: Dp) -> Unit,
    ) -> Unit
    ),
) {
    val useNativeTopBar = LocalNativeComponentsEnabled.current.value
    val nativeTopPadding = LocalNativeContentTopPadding.current
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        modifier = modifier,
        topBar = {
            if (!useNativeTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            modifier = titleModifier,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = typography.headlineSmall,
                        )
                    },
                    navigationIcon = {
                        Row {
                            sideMenuExpanded?.let { Spacer(Modifier.width(topAppBarStartPadding(it))) }
                            navigationIcon()
                        }
                    },
                    actions = {
                        AnimatedVisibility(LocalUsingOfflineCache.current.value) {
                            IconButton(
                                onClick = {},
                                enabled = false,
                            ) {
                                Icon(MaterialSymbols.Rounded.Cloud_off, null)
                            }
                        }
                        Spacer(Modifier.width(topAppBarEndPadding()))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                )
            }
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            val resolvedPadding =
                if (useNativeTopBar && nativeTopPadding != null) {
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        top = maxOf(innerPadding.calculateTopPadding(), nativeTopPadding),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = innerPadding.calculateBottomPadding(),
                    )
                } else {
                    innerPadding
                }
            content(resolvedPadding) { height ->
                if (!useNativeTopBar) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(height)
                            .then(
                                hazeState?.let {
                                    Modifier.enhancedHazeEffect(hazeState, colorScheme.secondaryContainer)
                                } ?: Modifier.background(colorScheme.secondaryContainer),
                            ),
                    )
                }
            }
        },
    )
}
