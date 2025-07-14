package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
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
    hazeState: HazeState,
    content: @Composable ((
        innerPadding: PaddingValues,
        topAppBarBackground: @Composable (height: Dp) -> Unit
    ) -> Unit)
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
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
                modifier = LocalTitleBarModifier.current.customTitleBarMouseEventHandler(),
                navigationIcon = {
                    Row {
                        sideMenuExpanded?.let { Spacer(Modifier.width(topAppBarStartPadding(it))) }
                        navigationIcon()
                    }
                },
                actions = { Spacer(Modifier.width(topAppBarEndPadding())) },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            content(innerPadding) { height ->
                Box(Modifier
                    .fillMaxWidth()
                    .height(height)
                    .enhancedHazeEffect(hazeState, colorScheme.secondaryContainer)
                )
            }
        }
    )
}