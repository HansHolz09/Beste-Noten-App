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
import com.hansholz.bestenotenapp.components.*
import com.hansholz.bestenotenapp.main.*
import com.hansholz.bestenotenapp.theme.*
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import com.hansholz.bestenotenapp.utils.topAppBarPadding
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
                    Text("Einstellungen", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                modifier = LocalTitleBarModifier.current.customTitleBarMouseEventHandler().topAppBarPadding(viewModel.mediumExpandedDrawerState.value.isOpen),
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

            var useSystemIsDark by LocalUseSystemIsDark.current
            var isDark by LocalIsDark.current
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
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                item {
                    PreferenceCategory("Design", Modifier.padding(horizontal = 15.dp))
                }
                item {
                    PreferenceItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = "Nachtmodus",
                        icon = Icons.Outlined.Brightness4,
                        position = PreferencePosition.Top,
                    ) {
                        Row {
                            FilledIconToggleButton(
                                checked = useSystemIsDark,
                                onCheckedChange = {
                                    useSystemIsDark = it
                                    settings["useSystemIsDark"] = it
                                },
                                shapes = IconButtonDefaults.toggleableShapes()
                            ) {
                                Icon(Icons.Outlined.BrightnessAuto, null)
                            }
                            FilledIconToggleButton(
                                checked = !useSystemIsDark && !isDark,
                                onCheckedChange = {
                                    useSystemIsDark = !it
                                    settings["useSystemIsDark"] = !it
                                    isDark = !it
                                    settings["isDark"] = !it
                                },
                                shapes = IconButtonDefaults.toggleableShapes()
                            ) {
                                Icon(Icons.Outlined.LightMode, null)
                            }
                            FilledIconToggleButton(
                                checked = !useSystemIsDark && isDark,
                                onCheckedChange = {
                                    useSystemIsDark = !it
                                    settings["useSystemIsDark"] = !it
                                    isDark = it
                                    settings["isDark"] = it
                                },
                                shapes = IconButtonDefaults.toggleableShapes()
                            ) {
                                Icon(Icons.Outlined.DarkMode, null)
                            }
                        }
                    }
                }
                if (supportsCustomColorScheme) {
                    settingsToggleItem(
                        checked = useCustomColorScheme,
                        onCheckedChange = {
                            useCustomColorScheme = it
                            settings["useCustomColorScheme"] = it
                        },
                        text = "Material-You",
                        icon = Icons.Outlined.InvertColors,
                        position = PreferencePosition.Middle,
                    )
                }
                settingsToggleItem(
                    checked = animationsEnabled,
                    onCheckedChange = {
                        animationsEnabled = it
                        settings["animationsEnabled"] = it
                    },
                    text = "Animationen",
                    icon = Icons.Outlined.Animation,
                    position = PreferencePosition.Middle,
                )
                settingsToggleItem(
                    checked = blurEnabled,
                    onCheckedChange = {
                        blurEnabled = it
                        settings["blurEnabled"] = it
                    },
                    text = "Unschärfe-Effekt",
                    icon = Icons.Outlined.BlurOn,
                    position = PreferencePosition.Middle,
                )
                settingsToggleItem(
                    checked = backgroundEnabled,
                    onCheckedChange = {
                        backgroundEnabled = it
                        settings["backgroundEnabled"] = it
                    },
                    text = "Hintergrundbild",
                    icon = Icons.Outlined.Texture,
                    position = PreferencePosition.Bottom,
                )
                item {
                    PreferenceCategory("Startseite", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showGreetings,
                    onCheckedChange = {
                        showGreetings = it
                        settings["showGreetings"] = it
                    },
                    text = "Begrüßung anzeigen",
                    icon = Icons.Outlined.WavingHand,
                    position = PreferencePosition.Top,
                )
                settingsToggleItem(
                    checked = showNewestGrades,
                    onCheckedChange = {
                        showNewestGrades = it
                        settings["showNewestGrades"] = it
                    },
                    text = "Neuste Noten anzeigen",
                    icon = Icons.Outlined.FiberNew,
                    position = PreferencePosition.Bottom,
                )
                item {
                    PreferenceCategory("Noten", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showGradeHistory,
                    onCheckedChange = {
                        showGradeHistory = it
                        settings["showGradeHistory"] = it
                    },
                    text = "Noten-Historien anzeigen",
                    icon = Icons.Outlined.History,
                    position = PreferencePosition.Top,
                )
                settingsToggleItem(
                    checked = showCollectionsWithoutGrades,
                    onCheckedChange = {
                        showCollectionsWithoutGrades = it
                        settings["showCollectionsWithoutGrades"] = it
                    },
                    text = "Leistungen ohne Noten anzeigen",
                    icon = Icons.Outlined.DisabledVisible,
                    position = PreferencePosition.Bottom,
                )
                item {
                    PreferenceCategory("Allgemein", Modifier.padding(horizontal = 15.dp))
                }
                settingsToggleItem(
                    checked = showTeachersWithFirstname,
                    onCheckedChange = {
                        showTeachersWithFirstname = it
                        settings["showTeachersWithFirstname"] = it
                    },
                    text = "Lehrer mit Vornamen anzeigen",
                    icon = Icons.Outlined.Title,
                )
                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
            Box(Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.secondaryContainer)
            )
        }
    )
}