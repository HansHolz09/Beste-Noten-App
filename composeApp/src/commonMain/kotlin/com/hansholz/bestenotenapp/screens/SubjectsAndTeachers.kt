@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import com.hansholz.bestenotenapp.utils.topAppBarPadding
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SubjectsAndTeachers(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

        var isLoading by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isLoading = true
            if (viewModel.finalGrades.isEmpty()) {
                viewModel.finalGrades.addAll(viewModel.api.finalgradesIndex().data)
            }
            if (viewModel.subjects.isEmpty()) {
                viewModel.subjects.addAll(viewModel.api.subjectsIndex().data)
            }
            isLoading = false
        }

        Scaffold(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-card"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Fächer und Lehrer",
                            modifier = Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-title"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ).skipToLookaheadSize(),
                            fontFamily = FontFamily.Serif,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
                var topPadding by remember { mutableStateOf(0.dp) }
                val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())

                val pagerState = rememberPagerState { 2 }
                HorizontalPager(pagerState, Modifier.hazeSource(viewModel.hazeBackgroundState)) {
                    AnimatedContent(isLoading) { targetState ->
                        Box(Modifier.fillMaxSize()) {
                            if (targetState) {
                                ContainedLoadingIndicator(Modifier.padding(contentPadding).align(Alignment.Center))
                            } else {
                                when(it) {
                                    0 -> {
                                        LazyColumn(contentPadding = contentPadding) {
                                            items(
                                                viewModel.subjects) { subject ->
                                                EnhancedAnimated(
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) {
                                                    ListItem(
                                                        headlineContent = {
                                                            Text(
                                                                text = "${subject.name} " +
                                                                        "(${viewModel.finalGrades
                                                                            .groupBy { it.subject }.map {
                                                                                it.key to it.value.map { it.teacher }.toSet()
                                                                            }
                                                                            .firstOrNull { it.first?.localId == subject.localId }
                                                                            ?.second
                                                                            ?.joinToString { (if (showTeachersWithFirstname) it?.forename else it?.forename?.take(1) + ".") + " " + it?.name }
                                                                            ?: "Kein Lehrer"})"
                                                            )
                                                        },
                                                        leadingContent = {
                                                            Text(subject.localId, textAlign = TextAlign.Center, modifier = Modifier.width(50.dp))
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    1 -> {
                                        LazyColumn(contentPadding = contentPadding) {
                                            items(viewModel.finalGrades.groupBy { it.teacher }.map { it.key to it.value.map { it.subject?.name }.toSet().joinToString() }) {
                                                EnhancedAnimated(
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) {
                                                    ListItem(
                                                        headlineContent = {
                                                            Text((if (showTeachersWithFirstname) it.first?.forename else it.first?.forename?.take(1) + ".") + " " + it.first?.name + " (" + it.second + ")")
                                                        },
                                                        leadingContent = {
                                                            Text(it.first?.localId ?: "", textAlign = TextAlign.Center, modifier = Modifier.width(50.dp))
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Box(Modifier
                    .fillMaxWidth()
                    .height(topPadding)
                    .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.secondaryContainer)
                )
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .onGloballyPositioned {
                            topPadding = with(density) { it.size.height.toDp() } + innerPadding.calculateTopPadding()
                        },
                    containerColor = Color.Transparent,
                    divider = { HorizontalDivider(thickness = 2.dp) }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        text = {
                            Text(
                                text = "Fächer",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        text = {
                            Text(
                                text = "Lehrer",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        )
    }
}