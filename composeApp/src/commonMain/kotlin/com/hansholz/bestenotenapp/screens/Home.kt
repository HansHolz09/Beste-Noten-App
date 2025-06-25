@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.grades
import bestenotenapp.composeapp.generated.resources.subjectsAndTeachers
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.repeatingBackground
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Screen
import com.hansholz.bestenotenapp.utils.*
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.imageResource
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Home(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToScreen: (Screen) -> Unit
) {
    with(sharedTransitionScope) {
        val scope = rememberCoroutineScope()
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val showGreetings by LocalShowGreetings.current
        val showNewestGrades by LocalShowNewestGrades.current

        var isGradesLoading by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                if (showNewestGrades) {
                    isGradesLoading = true
                    if (viewModel.startGradeCollections.isEmpty()) {
                        viewModel.startGradeCollections.addAll(viewModel.getCollections())
                    }
                    isGradesLoading = false
                }
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Startseite", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(400.dp),
                    modifier = Modifier.hazeSource(viewModel.hazeBackgroundState1),
                    contentPadding = innerPadding,
                ) {
                    if (showGreetings) {
                        item {
                            var greeting by remember { mutableStateOf("") }
                            LaunchedEffect(viewModel.user.value) {
                                if (viewModel.user.value?.students?.firstOrNull() != null) {
                                    greeting = getGreeting(viewModel.user.value?.students?.firstOrNull()?.forename ?: "du")
                                }
                            }
                            AnimatedContent(greeting) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(20.dp).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        greeting = getGreeting(viewModel.user.value?.students?.firstOrNull()?.forename ?: "du")
                                    },
                                    textAlign = TextAlign.Center,
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Companion.Cursive
                                )
                            }
                        }
                    }
                    item {
                        val imageBitmap = imageResource(Res.drawable.grades)
                        Box(Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .repeatingBackground(
                                imageBitmap = imageBitmap,
                                alpha = 0.2f,
                                scale = 0.75f,
                                offset = remember { Offset(x = Random.nextFloat() * imageBitmap.width, y = 0f) }
                            )
                            .border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(12.dp))
                            .clickable {
                                onNavigateToScreen(Screen.Grades)
                            }
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "grades-card"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        ) {
                            Column {
                                Box(Modifier
                                    .padding(10.dp)
                                    .padding(top = 10.dp)
                                    .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "Noten",
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(key = "grades-title"),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            )
                                            .skipToLookaheadSize(),
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 22.sp
                                    )
                                }
                                if (showNewestGrades) {
                                    AnimatedContent(isGradesLoading) { targetState ->
                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 100.dp)) {
                                            if (targetState) {
                                                ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                            } else {
                                                Column {
                                                    viewModel
                                                        .startGradeCollections
                                                        .filter { it.grades?.size != 0 }
                                                        .sortedByDescending { it.givenAt }
                                                        .take(5)
                                                        .forEach {
                                                            ListItem(
                                                                headlineContent = {
                                                                    Text("${it.subject?.name}: ${it.name}")
                                                                },
                                                                supportingContent = {
                                                                    Column {
                                                                        Text("${it.type} vom ${formateDate(it.givenAt)}")
                                                                    }
                                                                },
                                                                leadingContent = {
                                                                    Text(it.grades?.getOrNull(0)?.value ?: "🚫", textAlign = TextAlign.Center, modifier = Modifier.width(30.dp))
                                                                },
                                                                colors = ListItemDefaults.colors(Color.Transparent),
                                                                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState2)
                                                            )
                                                        }
                                                }
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = "Tippen, um deine Noten ansehen und analysieren zu können",
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    item {
                        val imageBitmap = imageResource(Res.drawable.subjectsAndTeachers)
                        Box(Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .repeatingBackground(
                                imageBitmap = imageBitmap,
                                alpha = 0.2f,
                                scale = 0.6f,
                                offset = remember { Offset(x = Random.nextFloat() * imageBitmap.width, y = -100f) }
                            )
                            .border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(12.dp))
                            .clickable {
                                onNavigateToScreen(Screen.SubjectsAndTeachers)
                            }
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-card"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        ) {
                            Column {
                                Box(Modifier
                                    .padding(10.dp)
                                    .padding(top = 10.dp)
                                    .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "Fächer und Lehrer",
                                        modifier = Modifier
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-title"),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            )
                                            .skipToLookaheadSize(),
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 22.sp
                                    )
                                }
                                Text(
                                    text = "Tippen, um einen Überblick über Fächer und Lehrer zu bekommen",
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Box(Modifier
                    .fillMaxWidth()
                    .height(innerPadding.calculateTopPadding())
                    .enhancedHazeEffect(viewModel.hazeBackgroundState1, colorScheme.secondaryContainer)
                )
            }
        )
    }
}