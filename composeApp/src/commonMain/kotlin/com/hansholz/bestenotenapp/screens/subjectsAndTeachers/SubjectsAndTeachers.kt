@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens.subjectsAndTeachers

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hansholz.bestenotenapp.api.models.Subject
import com.hansholz.bestenotenapp.api.models.Teacher
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedPagerState
import com.hansholz.bestenotenapp.components.rememberLazyListScrollSpeedState
import com.hansholz.bestenotenapp.main.LocalShowAllSubjects
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SubjectsAndTeachers(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val subjectsAndTeachersViewModel = viewModel { SubjectsAndTeachersViewModel(viewModel) }

        val scope = rememberCoroutineScope()
        val vibrator = rememberVibrator()
        val density = LocalDensity.current
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val showAllSubjects by LocalShowAllSubjects.current
        val showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

        TopAppBarScaffold(
            modifier =
                Modifier.enhancedSharedBounds(
                    sharedTransitionScope = sharedTransitionScope,
                    sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-card"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            title = "Fächer und Lehrer",
            titleModifier =
                Modifier
                    .enhancedSharedElement(
                        sharedTransitionScope = sharedTransitionScope,
                        sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ).skipToLookaheadSize(),
            navigationIcon = {
                EnhancedIconButton(
                    onClick = {
                        scope.launch {
                            viewModel.closeOrOpenDrawer(windowWithSizeClass)
                        }
                    },
                ) {
                    Icon(Icons.Filled.Menu, null)
                }
            },
            sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
            hazeState = viewModel.hazeBackgroundState,
        ) { innerPadding, topAppBarBackground ->
            val contentPadding = PaddingValues(top = subjectsAndTeachersViewModel.topPadding, bottom = innerPadding.calculateBottomPadding())

            val pagerState = rememberEnhancedPagerState(2)
            HorizontalPager(pagerState, Modifier.hazeSource(viewModel.hazeBackgroundState)) {
                EnhancedAnimatedContent(subjectsAndTeachersViewModel.isLoading) { targetState ->
                    Box(Modifier.fillMaxSize()) {
                        if (targetState) {
                            ContainedLoadingIndicator(Modifier.padding(contentPadding).align(Alignment.Center))
                        } else {
                            when (it) {
                                0 -> {
                                    val items =
                                        remember(viewModel.subjects, viewModel.subjectsAndTeachers, viewModel.isDemoAccount, showAllSubjects) {
                                            val subjectsAndTeachers = mutableStateListOf<Pair<Subject?, List<Teacher>?>>()
                                            subjectsAndTeachers.addAll(viewModel.subjectsAndTeachers)
                                            if (showAllSubjects && !viewModel.isDemoAccount.value) {
                                                subjectsAndTeachers.addAll(viewModel.subjects.filter { !subjectsAndTeachers.map { it.first }.contains(it) }.map { it to null })
                                            }
                                            subjectsAndTeachers.sortedBy { it.first?.name }
                                        }
                                    if (items.isEmpty()) {
                                        EmptyStateMessage(
                                            title = "Keine Fächer vorhanden",
                                            modifier = Modifier.padding(contentPadding),
                                        )
                                    } else {
                                        val lazyListState = rememberLazyListState()
                                        val speedState = rememberLazyListScrollSpeedState(lazyListState)
                                        LazyColumn(
                                            state = lazyListState,
                                            contentPadding = contentPadding,
                                        ) {
                                            items(items) { (subject, teachers) ->
                                                EnhancedAnimated(
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) { isAnimated ->
                                                    LaunchedEffect(Unit) {
                                                        if (lazyListState.isScrollInProgress && isAnimated && abs(speedState.pxPerSecond) > 4000) {
                                                            vibrator.enhancedVibrate(EnhancedVibrations.LOW_TICK)
                                                        }
                                                    }
                                                    ListItem(
                                                        headlineContent = {
                                                            Text(
                                                                text =
                                                                    "${subject?.name ?: "Unbekanntes Fach"} " +
                                                                        "(${teachers?.joinToString {
                                                                            (
                                                                                if (showTeachersWithFirstname) {
                                                                                    it.forename
                                                                                } else {
                                                                                    it.forename?.take(
                                                                                        1,
                                                                                    ) + "."
                                                                                }
                                                                            ) + " " + it.name
                                                                        }
                                                                            ?: "Kein Lehrer"})",
                                                            )
                                                        },
                                                        leadingContent = {
                                                            Box(Modifier.clip(ClamShell.toShape()).background(colorScheme.primaryContainer)) {
                                                                Text(
                                                                    text = subject?.localId ?: "?",
                                                                    color = colorScheme.onPrimaryContainer,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.width(50.dp).padding(vertical = 5.dp),
                                                                )
                                                            }
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    if (viewModel.teachersAndSubjects.isEmpty()) {
                                        EmptyStateMessage(
                                            title = "Keine Lehrer vorhanden",
                                            modifier = Modifier.padding(contentPadding),
                                        )
                                    } else {
                                        val lazyListState = rememberLazyListState()
                                        val speedState = rememberLazyListScrollSpeedState(lazyListState)
                                        LazyColumn(
                                            state = lazyListState,
                                            contentPadding = contentPadding,
                                        ) {
                                            items(viewModel.teachersAndSubjects) { (teacher, subjects) ->
                                                EnhancedAnimated(
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) { isAnimated ->
                                                    LaunchedEffect(Unit) {
                                                        if (lazyListState.isScrollInProgress && isAnimated && abs(speedState.pxPerSecond) > 4000) {
                                                            vibrator.enhancedVibrate(EnhancedVibrations.LOW_TICK)
                                                        }
                                                    }
                                                    ListItem(
                                                        headlineContent = {
                                                            Text(
                                                                (if (showTeachersWithFirstname) teacher?.forename else teacher?.forename?.take(1) + ".") + " " +
                                                                    teacher?.name + " (" + subjects.joinToString { it?.name ?: "unbekanntes Fach" } + ")",
                                                            )
                                                        },
                                                        leadingContent = {
                                                            Box(Modifier.clip(ClamShell.toShape()).background(colorScheme.primaryContainer)) {
                                                                Text(
                                                                    text = teacher?.localId ?: "",
                                                                    color = colorScheme.onPrimaryContainer,
                                                                    textAlign = TextAlign.Center,
                                                                    modifier = Modifier.width(50.dp).padding(vertical = 5.dp),
                                                                )
                                                            }
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent),
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
            }
            topAppBarBackground(subjectsAndTeachersViewModel.topPadding)
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier =
                    Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .onGloballyPositioned {
                            subjectsAndTeachersViewModel.topPadding = with(density) { it.size.height.toDp() } + innerPadding.calculateTopPadding()
                        },
                containerColor = Color.Transparent,
                divider = { HorizontalDivider(thickness = 2.dp) },
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
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
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
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
    }
}
