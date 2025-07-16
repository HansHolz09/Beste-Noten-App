@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.grades
import bestenotenapp.composeapp.generated.resources.subjectsAndTeachers
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.repeatingBackground
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Fragment
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.getGreeting
import dev.chrisbanes.haze.hazeSource
import kotlin.random.Random
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.kodein.emoji.compose.m3.TextWithNotoAnimatedEmoji

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Home(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToScreen: (Fragment) -> Unit
) {
    with(sharedTransitionScope) {
        val scope = rememberCoroutineScope()
        val hapticFeedback = LocalHapticFeedback.current
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val showGreetings by LocalShowGreetings.current
        val showNewestGrades by LocalShowNewestGrades.current

        var isGradesLoading by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.viewModelScope.launch {
                if (showNewestGrades) {
                    isGradesLoading = true
                    if (viewModel.startGradeCollections.isEmpty()) {
                        viewModel.getCollections()?.let { viewModel.startGradeCollections.addAll(it) }
                    }
                    isGradesLoading = false
                }
            }
        }

        TopAppBarScaffold(
            title = "Startseite",
            navigationIcon = {
                EnhancedIconButton(
                    onClick = {
                        scope.launch {
                            viewModel.closeOrOpenDrawer(windowWithSizeClass)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Menu, null)
                }
            },
            sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
            hazeState = viewModel.hazeBackgroundState1
        ) { innerPadding, topAppBarBackground ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(400.dp),
                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState1),
                contentPadding = innerPadding,
            ) {
                if (showGreetings) {
                    item {
                        var greeting by rememberSaveable { mutableStateOf("") }
                        LaunchedEffect(viewModel.user.value, viewModel.isBesteSchuleNotReachable.value) {
                            if (viewModel.user.value?.students?.firstOrNull() != null && greeting.isEmpty()) {
                                greeting = getGreeting(viewModel.user.value?.students?.firstOrNull()?.forename ?: "du")
                            } else if (viewModel.isBesteSchuleNotReachable.value) {
                                greeting = "beste.schule konnte nicht erreicht werden, somit können deine Daten nicht geladen werden." +
                                        "\n\nBitte überprüfe deine Internetverbindung und den Status von beste.schule." +
                                        "\nSollte es weiterhin nicht funktionieren, dann versuche dich erneut anzumelden."
                            }
                        }
                        AnimatedContent(greeting) {
                            TextWithNotoAnimatedEmoji(
                                text = it,
                                modifier = Modifier
                                    .animateItem()
                                    .animateContentSize()
                                    .padding(20.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        enabled = !viewModel.isBesteSchuleNotReachable.value
                                    ) {
                                        greeting = getGreeting(viewModel.user.value?.students?.firstOrNull()?.forename ?: "du")
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    },
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamilies.Schoolbell(),
                                style = typography.titleLarge
                            )
                        }
                    }
                }
                item {
                    val imageBitmap = imageResource(Res.drawable.grades)
                    Box(Modifier
                        .animateItem()
                        .animateContentSize()
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
                            onNavigateToScreen(Fragment.Grades)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                    style = typography.headlineSmall
                                )
                            }
                            if (showNewestGrades) {
                                AnimatedContent(isGradesLoading) { targetState ->
                                    if (targetState) {
                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 100.dp)) {
                                            ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                        }
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
                                                    )
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
                        .animateItem()
                        .animateContentSize()
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
                            onNavigateToScreen(Fragment.SubjectsAndTeachers)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                    fontFamily = FontFamilies.KeaniaOne(),
                                    style = typography.headlineSmall
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
            topAppBarBackground(innerPadding.calculateTopPadding())
        }
    }
}