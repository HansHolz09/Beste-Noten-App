@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.ViewModel
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.nomanr.animate.compose.presets.specials.JackInTheBox
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: ViewModel
) {
    val scope = rememberCoroutineScope()
    val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    EnhancedAnimated(
                        preset = JackInTheBox(),
                    ) {
                        Text("Startseite", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.closeOrOpenDrawer(windowWithSizeClass)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Menu, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
            }
            Box(Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .hazeEffect(viewModel.hazeBackgroundState, HazeStyle(colorScheme.secondaryContainer, emptyList())) {
                    noiseFactor = 0f
                    progressive = HazeProgressive.verticalGradient(startIntensity = 1f)
                }
            )
        }
    )
}