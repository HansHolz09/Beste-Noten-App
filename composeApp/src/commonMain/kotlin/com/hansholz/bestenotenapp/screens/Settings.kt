@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.*
import com.hansholz.bestenotenapp.theme.*
import com.nomanr.animate.compose.presets.specials.JackInTheBox
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
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
                        Text("Einstellungen", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
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

            var isDark by LocalThemeIsDark.current
            var useCustomColorScheme by LocalUseCustomColorScheme.current
            val supportsCustomColorScheme by LocalSupportsCustomColorScheme.current
            var animationsEnabled by LocalAnimationsEnabled.current
            var blurEnabled by LocalBlurEnabled.current
            var backgroundEnabled by LocalBackgroundEnabled.current
            var showGreetings by LocalShowGreetings.current
            var showNewestGrades by LocalShowNewestGrades.current
            var showGradeHistory by LocalShowGradeHistory.current
            var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
            var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
            val settings = Settings()

            LazyColumn(
                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(Modifier.height(5.dp))
                }
                item {
                    Text("Design", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                settingsToggleItem(
                    checked = isDark,
                    onCheckedChange = {
                        isDark = it
                        settings["isDark"] = it
                    },
                    text = "Dunkles-Design",
                    checkedIcon = Icons.Outlined.DarkMode,
                    uncheckedIcon = Icons.Outlined.WbSunny
                )
                if (supportsCustomColorScheme) {
                    settingsToggleItem(
                        checked = useCustomColorScheme,
                        onCheckedChange = {
                            useCustomColorScheme = it
                            settings["useCustomColorScheme"] = it
                        },
                        text = "Material-You",
                        checkedIcon = Icons.Outlined.InvertColors,
                        uncheckedIcon = Icons.Outlined.InvertColorsOff
                    )
                }
                settingsToggleItem(
                    checked = animationsEnabled,
                    onCheckedChange = {
                        animationsEnabled = it
                        settings["animationsEnabled"] = it
                    },
                    text = "Animationen",
                    checkedIcon = Icons.Outlined.Animation,
                    uncheckedIcon = Icons.Outlined.MotionPhotosOff
                )
                settingsToggleItem(
                    checked = blurEnabled,
                    onCheckedChange = {
                        blurEnabled = it
                        settings["blurEnabled"] = it
                    },
                    text = "Unschärfe-Effekt",
                    checkedIcon = Icons.Outlined.BlurOn,
                    uncheckedIcon = Icons.Outlined.BlurOff
                )
                settingsToggleItem(
                    checked = backgroundEnabled,
                    onCheckedChange = {
                        backgroundEnabled = it
                        settings["backgroundEnabled"] = it
                    },
                    text = "Hintergrundbild",
                    checkedIcon = Icons.Outlined.Texture,
                    uncheckedIcon = Icons.Outlined.NotInterested
                )
                item {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    Text("Startseite", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                settingsToggleItem(
                    checked = showGreetings,
                    onCheckedChange = {
                        showGreetings = it
                        settings["showGreetings"] = it
                    },
                    text = "Begrüßung anzeigen"
                )
                settingsToggleItem(
                    checked = showNewestGrades,
                    onCheckedChange = {
                        showNewestGrades = it
                        settings["showNewestGrades"] = it
                    },
                    text = "Neuste Noten anzeigen"
                )
                item {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    Text("Noten", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                settingsToggleItem(
                    checked = showGradeHistory,
                    onCheckedChange = {
                        showGradeHistory = it
                        settings["showGradeHistory"] = it
                    },
                    text = "Noten-Historien anzeigen"
                )
                settingsToggleItem(
                    checked = showCollectionsWithoutGrades,
                    onCheckedChange = {
                        showCollectionsWithoutGrades = it
                        settings["showCollectionsWithoutGrades"] = it
                    },
                    text = "Leistungen ohne Noten anzeigen"
                )
                item {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    Text("Allgemein", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                settingsToggleItem(
                    checked = showTeachersWithFirstname,
                    onCheckedChange = {
                        showTeachersWithFirstname = it
                        settings["showTeachersWithFirstname"] = it
                    },
                    text = "Lehrer mit Vornamen anzeigen"
                )
            }
            Box(Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.secondaryContainer)
            )
        }
    )
}