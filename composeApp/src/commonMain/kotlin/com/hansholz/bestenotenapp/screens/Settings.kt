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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
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
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Dunkles-Design", fontSize = 18.sp)
                        Switch(
                            checked = isDark,
                            onCheckedChange = {
                                isDark = it
                                settings["isDark"] = it
                            },
                            thumbContent =
                                if (isDark) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.DarkMode,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.WbSunny,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                if (supportsCustomColorScheme) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Material-You", fontSize = 18.sp)
                            Switch(
                                checked = useCustomColorScheme,
                                onCheckedChange = {
                                    useCustomColorScheme = it
                                    settings["useCustomColorScheme"] = it
                                },
                                thumbContent =
                                    if (useCustomColorScheme) {
                                        {
                                            Icon(
                                                imageVector = Icons.Outlined.InvertColors,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        {
                                            Icon(
                                                imageVector = Icons.Outlined.InvertColorsOff,
                                                contentDescription = null,
                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                            )
                                        }
                                    },
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Animationen", fontSize = 18.sp)
                        Switch(
                            checked = animationsEnabled,
                            onCheckedChange = {
                                animationsEnabled = it
                                settings["animationsEnabled"] = it
                            },
                            thumbContent =
                                if (animationsEnabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Animation,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.MotionPhotosOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Unschärfe-Effekt", fontSize = 18.sp)
                        Switch(
                            checked = blurEnabled,
                            onCheckedChange = {
                                blurEnabled = it
                                settings["blurEnabled"] = it
                            },
                            thumbContent =
                                if (blurEnabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.BlurOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.BlurOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Hintergrundbild", fontSize = 18.sp)
                        Switch(
                            checked = backgroundEnabled,
                            onCheckedChange = {
                                backgroundEnabled = it
                                settings["backgroundEnabled"] = it
                            },
                            thumbContent =
                                if (backgroundEnabled) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Texture,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.NotInterested,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    Text("Noten", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Noten-Historien anzeigen", fontSize = 18.sp)
                        Switch(
                            checked = showGradeHistory,
                            onCheckedChange = {
                                showGradeHistory = it
                                settings["showGradeHistory"] = it
                            },
                            thumbContent =
                                if (showGradeHistory) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Leistungen ohne Noten anzeigen", fontSize = 18.sp)
                        Switch(
                            checked = showCollectionsWithoutGrades,
                            onCheckedChange = {
                                showCollectionsWithoutGrades = it
                                settings["showCollectionsWithoutGrades"] = it
                            },
                            thumbContent =
                                if (showCollectionsWithoutGrades) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(thickness = 2.dp)
                }
                item {
                    Text("Allgemein", Modifier.padding(horizontal = 15.dp).padding(top = 10.dp), colorScheme.primary)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Lehrer mit Vornamen anzeigen", fontSize = 18.sp)
                        Switch(
                            checked = showTeachersWithFirstname,
                            onCheckedChange = {
                                showTeachersWithFirstname = it
                                settings["showTeachersWithFirstname"] = it
                            },
                            thumbContent =
                                if (showTeachersWithFirstname) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                },
                        )
                    }
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