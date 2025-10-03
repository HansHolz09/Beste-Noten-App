package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DisabledVisible
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PlaylistRemove
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.api.models.Year
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.GradeValueBox
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedPagerState
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.filterHistory
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.isScrollingUp
import com.hansholz.bestenotenapp.utils.normalizeGrade
import com.hansholz.bestenotenapp.utils.translateHistoryBody
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.DefaultVerticalBarPlotEntry
import io.github.koalaplot.core.bar.DefaultVerticalBarPosition
import io.github.koalaplot.core.bar.GroupedVerticalBarPlot
import io.github.koalaplot.core.bar.StackedVerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlotGroupedPointEntry
import io.github.koalaplot.core.bar.VerticalBarPlotStackedPointEntry
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot
import io.github.koalaplot.core.polar.DefaultPolarPoint
import io.github.koalaplot.core.polar.PolarGraph
import io.github.koalaplot.core.polar.PolarPlotSeries
import io.github.koalaplot.core.polar.PolarPoint
import io.github.koalaplot.core.polar.rememberCategoryAngularAxisModel
import io.github.koalaplot.core.polar.rememberFloatRadialAxisModel
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.math.ceil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class, ExperimentalKoalaPlotApi::class
)
@Composable
fun Grades(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val hapticFeedback = LocalHapticFeedback.current
        val layoutDirection = LocalLayoutDirection.current
        @Suppress("DEPRECATION")
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        var showGradeHistory by LocalShowGradeHistory.current
        var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
        var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
        val settings = Settings()

        var isLoading by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        val selectedYears = remember { mutableStateListOf<Year>() }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.Unconfined) {
                isLoading = true
                if (viewModel.years.isEmpty()) {
                    viewModel.getYears()?.let { viewModel.years.addAll(it) }
                }
                if (viewModel.gradeCollections.isEmpty() && viewModel.years.isNotEmpty()) {
                    viewModel.getCollections(listOf(viewModel.years.last()))?.let { viewModel.gradeCollections.addAll(it) }
                }
                if (viewModel.years.isNotEmpty()) {
                    selectedYears.clear()
                    selectedYears.add(viewModel.years.last())
                }
                isLoading = false
            }
        }

        TopAppBarScaffold(
            modifier = Modifier.enhancedSharedBounds(
                sharedTransitionScope = sharedTransitionScope,
                sharedContentState = rememberSharedContentState(key = "grades-card"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            title = "Noten",
            titleModifier = Modifier.enhancedSharedElement(
                sharedTransitionScope = sharedTransitionScope,
                sharedContentState = rememberSharedContentState(key = "grades-title"),
                animatedVisibilityScope = animatedVisibilityScope
            ).skipToLookaheadSize(),
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
            hazeState = viewModel.hazeBackgroundState
        ) { innerPadding, topAppBarBackground ->
            Box(Modifier.fillMaxSize()) {
                var topPadding by remember { mutableStateOf(0.dp) }
                var toolbarPadding by remember { mutableStateOf(0.dp) }
                val toolbarContentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())
                val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding() + toolbarPadding)
                val verticalPadding = PaddingValues(start = innerPadding.calculateStartPadding(layoutDirection), end = innerPadding.calculateEndPadding(layoutDirection))

                val pagerState = rememberEnhancedPagerState(2)
                var userScrollEnabled by remember { mutableStateOf(true) }
                var contentBlurred by remember { mutableStateOf(false) }
                val contentBlurRadius = animateDpAsState(if (contentBlurred) 10.dp else 0.dp)
                val firstLazyListState = rememberLazyListState()
                val secondLazyListState = rememberLazyListState()
                val currentLazyListState = when(pagerState.currentPage) {
                    0 -> firstLazyListState
                    1 -> secondLazyListState
                    else -> rememberLazyListState()
                }
                val items = remember(viewModel.gradeCollections, selectedYears.size, showCollectionsWithoutGrades, searchQuery, isLoading) {
                     viewModel
                        .gradeCollections
                        .toSet()
                        .filter { selectedYears.map { it.id }.contains(it.interval?.yearId) }
                        .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
                        .filter { (it.name ?: "").contains(searchQuery, true) }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.hazeSource(viewModel.hazeBackgroundState).enhancedHazeEffect(blurRadius = contentBlurRadius.value),
                    userScrollEnabled = userScrollEnabled
                ) { currentPage ->
                    AnimatedContent(isLoading || items.isEmpty()) { targetState ->
                        Box(Modifier.fillMaxSize()) {
                            if (targetState) {
                                AnimatedContent(isLoading) { isLoading ->
                                    if (isLoading) {
                                        Box(
                                            modifier = Modifier.padding(contentPadding).fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ContainedLoadingIndicator()
                                        }
                                    } else {
                                        EmptyStateMessage(
                                            title = if (searchQuery.isEmpty()) "Keine Noten vorhanden" else "Keine Noten gefunden",
                                            icon = if (searchQuery.isEmpty()) Icons.Outlined.PlaylistRemove else Icons.Outlined.SearchOff,
                                            modifier = Modifier.padding(contentPadding).consumeWindowInsets(contentPadding).imePadding()
                                        )
                                    }
                                }
                            } else {
                                when(currentPage) {
                                    0 -> {
                                        LazyColumn(
                                            state = firstLazyListState,
                                            contentPadding = contentPadding,
                                            userScrollEnabled = userScrollEnabled
                                        ) {
                                            items(items.sortedByDescending { it.givenAt }.toList()) {
                                                EnhancedAnimated(
                                                    modifier = Modifier.padding(verticalPadding),
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) { isAnimated ->
                                                    LaunchedEffect(Unit) {
                                                        if (firstLazyListState.isScrollInProgress && isAnimated) {
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                                        }
                                                    }
                                                    ListItem(
                                                        headlineContent = {
                                                            Text("${it.subject?.name}: ${it.name}")
                                                        },
                                                        supportingContent = {
                                                            Column {
                                                                Text("${it.type} vom ${formateDate(it.givenAt)}")

                                                                val histories = it.grades?.getOrNull(0)?.histories

                                                                if (histories?.isEmpty() == false && showGradeHistory) {
                                                                    Spacer(Modifier.height(10.dp))
                                                                    Text("Historie deiner Note:")
                                                                    histories.filterHistory().forEach {
                                                                        Text("${if (showTeachersWithFirstname) it.conductor?.forename else it.conductor?.forename?.take(1) + "."} ${it.conductor?.name}: ${translateHistoryBody(it.body)}")
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        leadingContent = {
                                                            GradeValueBox(it.grades?.getOrNull(0)?.value)
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent),
                                                        modifier = Modifier.hazeSource(viewModel.hazeBackgroundState2)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    1 -> {
                                        LazyColumn(
                                            state = secondLazyListState,
                                            modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
                                            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                                            userScrollEnabled = userScrollEnabled
                                        ) {
                                            items
                                                .sortedWith(compareBy({ it.subject?.name }, { it.givenAt }))
                                                .groupBy { it.subject?.name }
                                                .toList()
                                                .forEach { (title, items) ->
                                                    stickyHeader {
                                                        EnhancedAnimated(
                                                            preset = ZoomIn(),
                                                            durationMillis = 200,
                                                        ) { isAnimated ->
                                                            LaunchedEffect(Unit) {
                                                                if (secondLazyListState.isScrollInProgress && isAnimated) {
                                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                                                }
                                                            }
                                                            Column(
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .height(56.dp)
                                                            ) {
                                                                HorizontalDivider(thickness = 1.dp)
                                                                Box(Modifier.weight(1f)) {
                                                                    Box(
                                                                        Modifier
                                                                            .fillMaxSize()
                                                                            .enhancedHazeEffect(viewModel.hazeBackgroundState3, colorScheme.secondaryContainer)
                                                                            .enhancedHazeEffect(viewModel.hazeBackgroundState2, colorScheme.secondaryContainer) {
                                                                                mask = Brush.verticalGradient(
                                                                                    colors = listOf(Color.Transparent, Color.Red)
                                                                                )
                                                                            }
                                                                    )
                                                                    Text(
                                                                        text = title ?: "Kein Fach",
                                                                        modifier = Modifier
                                                                            .align(Alignment.CenterStart)
                                                                            .padding(start = 16.dp),
                                                                        style = typography.titleMedium
                                                                    )
                                                                }
                                                                HorizontalDivider(thickness = 1.dp)
                                                            }
                                                        }
                                                    }
                                                    items(items) {
                                                        EnhancedAnimated(
                                                            modifier = Modifier.padding(verticalPadding),
                                                            preset = ZoomIn(),
                                                            durationMillis = 200,
                                                        ) { isAnimated ->
                                                            LaunchedEffect(Unit) {
                                                                if (secondLazyListState.isScrollInProgress && isAnimated) {
                                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                                                }
                                                            }
                                                            ListItem(
                                                                headlineContent = {
                                                                    Text("${it.name} - ${it.type}")
                                                                },
                                                                supportingContent = {
                                                                    Column {
                                                                        Text("Gegeben am ${formateDate(it.givenAt)}")

                                                                        val histories = it.grades?.getOrNull(0)?.histories

                                                                        if (histories?.isEmpty() == false && showGradeHistory) {
                                                                            Spacer(Modifier.height(10.dp))
                                                                            Text("Historie deiner Note:")
                                                                            histories.filterHistory().forEach {
                                                                                Text("${if (showTeachersWithFirstname) it.conductor?.forename else it.conductor?.forename?.take(1) + "."} ${it.conductor?.name}: ${translateHistoryBody(it.body)}")
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                                leadingContent = {
                                                                    GradeValueBox(it.grades?.getOrNull(0)?.value)
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
                            }
                        }
                    }
                }
                topAppBarBackground(topPadding)
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .padding(verticalPadding)
                        .padding(top = innerPadding.calculateTopPadding())
                        .onGloballyPositioned {
                            topPadding = with(density) { it.size.height.toDp() } + innerPadding.calculateTopPadding()
                        },
                    containerColor = Color.Transparent,
                    divider = {
                        HorizontalDivider(thickness = if (pagerState.currentPage == 0) 2.dp else 1.dp)
                    }
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
                                text = "Nach Datum",
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
                                text = "Nach Fächern",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }

                SharedTransitionLayout(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -(toolbarContentPadding.calculateBottomPadding() + 12.dp))
                        .padding(verticalPadding)
                        .consumeWindowInsets(toolbarContentPadding)
                        .imePadding()
                ) {
                    var state by remember { mutableStateOf(0) }
                    val sharedContentState = rememberSharedContentState(key = "toolbar-card")
                    val ime = WindowInsets.ime

                    var backProgress by remember { mutableFloatStateOf(0f) }
                    var isBackInProgress by remember { mutableStateOf(false) }
                    PredictiveBackHandler(enabled = state != 0 && !isLoading) { progressFlow ->
                        try {
                            isBackInProgress = true

                            progressFlow.collect { event ->
                                backProgress = event.progress
                            }

                            scope.launch {
                                searchQuery = ""
                                state = 0
                                contentBlurred = false
                                delay(250)
                                if (state == 0) userScrollEnabled = true
                                isBackInProgress = false
                                backProgress = 0f
                            }
                        } catch (_: CancellationException) {
                            isBackInProgress = false
                            backProgress = 0f
                        }
                    }
                    val backHandlingModifier = if (isBackInProgress) {
                        Modifier.scale(1f - (backProgress * 0.2f))
                    } else {
                        Modifier
                    }

                    AnimatedContent(
                        targetState = state,
                        modifier = Modifier.padding(top = topPadding + 24.dp + innerPadding.calculateBottomPadding()).onGloballyPositioned {
                            toolbarPadding = with(density) { ime.getBottom(density).toDp() + it.size.height.toDp() + 12.dp }
                        },
                        contentAlignment = Alignment.BottomCenter,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith
                                    fadeOut(animationSpec = tween(250)) using
                                    SizeTransform(
                                        clip = false,
                                        sizeAnimationSpec = { _, _ ->
                                            spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                        }
                                    )
                        },
                    ) { targetState ->
                        when (targetState) {
                            0 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    HorizontalFloatingToolbar(
                                        expanded = currentLazyListState.isScrollingUp(),
                                        modifier = Modifier
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                renderInOverlayDuringTransition = false
                                            )
                                            .clip(FloatingToolbarDefaults.ContainerShape).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(Color.Transparent),
                                        leadingContent = {
                                            EnhancedIconButton(
                                                onClick = {
                                                    state = 1
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                },
                                                enabled = !isLoading
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Search,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                            EnhancedIconButton(
                                                onClick = {
                                                    state = 2
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                    userScrollEnabled = false
                                                    contentBlurred = true
                                                },
                                                enabled = viewModel.years.isNotEmpty() && !isLoading
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.CalendarMonth,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                        },
                                        trailingContent = {
                                            EnhancedIconButton(
                                                onClick = {
                                                    scope.launch {
                                                        isLoading = true
                                                        viewModel.gradeCollections.clear()
                                                        if (viewModel.allGradeCollectionsLoaded.value) {
                                                            viewModel.getCollections(viewModel.years)?.let {
                                                                viewModel.gradeCollections.addAll(it)
                                                                isLoading = false
                                                            }
                                                        } else {
                                                            viewModel.getCollections(listOf(viewModel.years.last()))?.let {
                                                                viewModel.gradeCollections.addAll(it)
                                                                isLoading = false
                                                            }
                                                        }
                                                    }
                                                },
                                                enabled = viewModel.years.isNotEmpty() && !isLoading
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Refresh,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                            EnhancedIconButton(
                                                onClick = {
                                                    state = 3
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                    userScrollEnabled = false
                                                    contentBlurred = true
                                                },
                                                enabled = !isLoading
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Settings,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                        },
                                        collapsedShadowElevation = 0.dp,
                                        expandedShadowElevation = 0.dp
                                    ) {
                                        EnhancedIconButton(
                                            onClick = {
                                                state = 4
                                                isBackInProgress = false
                                                backProgress = 0f
                                                userScrollEnabled = false
                                                contentBlurred = true
                                            },
                                            enabled = !isLoading,
                                            modifier = Modifier.width(64.dp),
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = colorScheme.primary.copy(0.5f),
                                                disabledContainerColor = Color.Transparent
                                            )
                                        ) { enabled ->
                                            Icon(
                                                imageVector = Icons.Outlined.BarChart,
                                                contentDescription = null,
                                                tint = if (enabled) colorScheme.onPrimary else colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                            )
                                            .then(backHandlingModifier)
                                            .padding(horizontal = 12.dp)
                                            .sizeIn(maxWidth = 500.dp)
                                            .clip(RoundedCornerShape(16.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val focusRequester = remember { FocusRequester() }
                                            LaunchedEffect(Unit) {
                                                focusRequester.requestFocus()
                                            }
                                            EnhancedIconButton(
                                                onClick = {},
                                                enabled = false
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Search,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                            BasicTextField(
                                                value = searchQuery,
                                                onValueChange = { searchQuery = it },
                                                modifier = Modifier.weight(1f).padding(vertical = 15.dp).focusRequester(focusRequester),
                                                singleLine = true,
                                                textStyle = TextStyle.Default.copy(colorScheme.onPrimaryContainer, 20.sp),
                                                cursorBrush = SolidColor(colorScheme.onPrimaryContainer)
                                            )
                                            EnhancedIconButton(
                                                onClick = {
                                                    scope.launch {
                                                        searchQuery = ""
                                                        state = 0
                                                        userScrollEnabled = false
                                                        delay(250)
                                                        if (state == 0) userScrollEnabled = true
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Close,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                            )
                                            .then(backHandlingModifier)
                                            .padding(horizontal = 12.dp)
                                            .sizeIn(maxWidth = 500.dp)
                                            .clip(RoundedCornerShape(16.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent)
                                    ) {
                                        LaunchedEffect(Unit) {
                                            if (!viewModel.allGradeCollectionsLoaded.value) {
                                                isLoading = true
                                                viewModel.gradeCollections.clear()
                                                viewModel.getCollections(viewModel.years)?.let {
                                                    viewModel.gradeCollections.addAll(it)
                                                    viewModel.allGradeCollectionsLoaded.value = true
                                                    isLoading = false
                                                }
                                            }
                                        }
                                        Text(
                                            text = "Jahre",
                                            modifier = Modifier.padding(15.dp).align(Alignment.CenterHorizontally),
                                            color = colorScheme.onPrimaryContainer,
                                            style = typography.headlineSmall,
                                        )
                                        ProvideTextStyle(LocalTextStyle.current.copy(colorScheme.onPrimaryContainer)) {
                                            LazyColumn(
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                items(viewModel.years) { year ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        EnhancedCheckbox(
                                                            checked = selectedYears.contains(year),
                                                            onCheckedChange = {
                                                                if (selectedYears.contains(year)) {
                                                                    selectedYears.remove(year)
                                                                } else {
                                                                    selectedYears.add(year)
                                                                }
                                                            },
                                                            enabled = !isLoading
                                                        )
                                                        Text(
                                                            text = "${year.name} (${formateDate(year.from)} - ${formateDate(year.to)})",
                                                            style = typography.bodyLarge
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    state = 0
                                                    contentBlurred = false
                                                    delay(250)
                                                    if (state == 0) userScrollEnabled = true
                                                }
                                            },
                                            enabled = !isLoading,
                                            modifier = Modifier.padding(10.dp).align(Alignment.End)
                                        ) {
                                            AnimatedContent(isLoading) {
                                                if (it) {
                                                    CircularWavyProgressIndicator()
                                                } else {
                                                    Text("Schließen")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            3 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                            )
                                            .then(backHandlingModifier)
                                            .padding(horizontal = 12.dp)
                                            .sizeIn(maxWidth = 500.dp)
                                            .clip(RoundedCornerShape(16.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent)
                                    ) {
                                        Text(
                                            text = "Einstellungen",
                                            modifier = Modifier.padding(15.dp).align(Alignment.CenterHorizontally),
                                            color = colorScheme.onPrimaryContainer,
                                            style = typography.headlineSmall,
                                        )
                                        ProvideTextStyle(LocalTextStyle.current.copy(colorScheme.onPrimaryContainer)) {
                                            LazyColumn(
                                                modifier = Modifier.weight(1f, false),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                settingsToggleItem(
                                                    checked = showGradeHistory,
                                                    onCheckedChange = {
                                                        showGradeHistory = it
                                                        settings["showGradeHistory"] = it
                                                    },
                                                    text = "Noten-Historien anzeigen",
                                                    icon = Icons.Outlined.History,
                                                    textModifier = Modifier.skipToLookaheadSize(),
                                                    position = if (viewModel.isDemoAccount.value) PreferencePosition.Single else PreferencePosition.Top,
                                                )
                                                if (!viewModel.isDemoAccount.value) {
                                                    settingsToggleItem(
                                                        checked = showCollectionsWithoutGrades,
                                                        onCheckedChange = {
                                                            showCollectionsWithoutGrades = it
                                                            settings["showCollectionsWithoutGrades"] = it
                                                        },
                                                        text = "Leistungen ohne Noten anzeigen",
                                                        icon = Icons.Outlined.DisabledVisible,
                                                        textModifier = Modifier.skipToLookaheadSize(),
                                                        position = PreferencePosition.Middle,
                                                    )
                                                    settingsToggleItem(
                                                        checked = showTeachersWithFirstname,
                                                        onCheckedChange = {
                                                            showTeachersWithFirstname = it
                                                            settings["showTeachersWithFirstname"] = it
                                                        },
                                                        text = "Lehrer mit Vornamen anzeigen",
                                                        icon = Icons.Outlined.Title,
                                                        textModifier = Modifier.skipToLookaheadSize(),
                                                        position = PreferencePosition.Bottom,
                                                    )
                                                }
                                            }
                                        }
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    state = 0
                                                    contentBlurred = false
                                                    delay(250)
                                                    if (state == 0) userScrollEnabled = true
                                                }
                                            },
                                            modifier = Modifier.padding(10.dp).align(Alignment.End)
                                        ) {
                                            Text("Schließen")
                                        }
                                    }
                                }
                            }
                            4 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val hazeState = rememberHazeState()
                                    Card(
                                        modifier = Modifier
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                            )
                                            .then(backHandlingModifier)
                                            .padding(horizontal = 12.dp)
                                            .sizeIn(maxWidth = 600.dp)
                                            .clip(RoundedCornerShape(16.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.surfaceContainerHighest),
                                        colors = CardDefaults.cardColors(Color.Transparent)
                                    ) {
                                        Box {
                                            var analyzeYears by remember { mutableStateOf(false) }
                                            var filterSubjects by remember { mutableStateOf(false) }
                                            var filterShown by remember { mutableStateOf(true) }
                                            val deselectedSubjects = remember { mutableStateListOf<String>() }
                                            var titleHeight by remember { mutableStateOf(0.dp) }
                                            var closeBarHeight by remember { mutableStateOf(0.dp) }
                                            val firstLazyListState = rememberLazyListState()
                                            val secondLazyListState = rememberLazyListState()
                                            val lazyListState = if (analyzeYears) secondLazyListState else firstLazyListState

                                            val filteredGrades = viewModel.gradeCollections
                                                .toSet().filter { selectedYears.map { it.id }.contains(it.interval?.yearId) }
                                                .filter { !it.grades.isNullOrEmpty() && it.grades.firstOrNull()?.value?.take(1)?.toIntOrNull() != null }
                                            val allFilteredGrades = viewModel.gradeCollections
                                                .asSequence().filter { it.interval?.yearId != null && !it.grades.isNullOrEmpty() }

                                            AnimatedContent(analyzeYears) { analyzeYears ->
                                                if (!analyzeYears) {
                                                    LazyColumn(
                                                        modifier = Modifier.hazeSource(hazeState),
                                                        state = firstLazyListState,
                                                        contentPadding = PaddingValues(top = titleHeight, bottom = closeBarHeight),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        item {
                                                            val grades = filteredGrades
                                                                .filter { !filterSubjects || !deselectedSubjects.contains(it.subject?.name) }
                                                                .map { it.grades!![0].value.take(1).toIntOrNull() ?: 0 }.sortedBy { it }.groupBy { it }.toList()
                                                            val barChartEntries = buildList {
                                                                grades.map { (int, grade) ->
                                                                    add(DefaultVerticalBarPlotEntry(int.toFloat(), DefaultVerticalBarPosition(0f, grade.size.toFloat())))
                                                                }
                                                            }
                                                            if (grades.isNotEmpty()) {
                                                                XYGraph(
                                                                    xAxisModel = FloatLinearAxisModel(
                                                                        if (grades.isNotEmpty()) (grades.minOf { it.first }.toFloat() - 1f)..(grades.maxOf { it.first }.toFloat() + 1f) else 0f..1f,
                                                                        minimumMajorTickIncrement = 1f,
                                                                        minimumMajorTickSpacing = 10.dp,
                                                                        minorTickCount = 0
                                                                    ),
                                                                    yAxisModel = FloatLinearAxisModel(
                                                                        if (grades.isNotEmpty()) 0f..grades.maxOf { it.second.size }.toFloat() else 0f..1f,
                                                                        minimumMajorTickIncrement = 1f,
                                                                        minorTickCount = 0
                                                                    ),
                                                                    modifier = Modifier.padding(10.dp).height(400.dp),
                                                                    xAxisLabels = {
                                                                        try {
                                                                            if (it != 0f && it != (grades.maxOf { it.first }.toFloat() + 1f)) it.toString(0) else ""
                                                                        } catch (_: Exception) {
                                                                            ""
                                                                        }
                                                                    },
                                                                    xAxisTitle = null,
                                                                    yAxisLabels = { it.toString(0) },
                                                                    verticalMajorGridLineStyle = null,
                                                                ) {
                                                                    VerticalBarPlot(
                                                                        barChartEntries,
                                                                        bar = { index ->
                                                                            DefaultVerticalBar(
                                                                                brush = SolidColor(colorScheme.primary),
                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                shape = RoundedCornerShape(8.dp)
                                                                            ) {
                                                                                Surface(
                                                                                    shadowElevation = 2.dp,
                                                                                    shape = MaterialTheme.shapes.small,
                                                                                    color = colorScheme.surfaceContainerHighest,
                                                                                ) {
                                                                                    Box(Modifier.padding(5.dp)) {
                                                                                        Text(grades[index].second.size.toString())
                                                                                    }
                                                                                }
                                                                            }
                                                                        },
                                                                    )
                                                                }
                                                            } else {
                                                                Text("Noch keine Noten vorhanden", modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    /* Pre-generated by AI */
                                                    AnimatedContent(isLoading) { targetState ->
                                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 300.dp)) {
                                                            if (targetState) {
                                                                ContainedLoadingIndicator(Modifier.align(Alignment.Center).padding(top = titleHeight, bottom = closeBarHeight))
                                                            } else {
                                                                val gradeCollections = viewModel.gradeCollections
                                                                val years = viewModel.years
                                                                val processedData = remember(gradeCollections, years, filterSubjects, deselectedSubjects.size) {
                                                                    val allGradesByYear = allFilteredGrades
                                                                        .filter { !filterSubjects || !deselectedSubjects.contains(it.subject?.name) }
                                                                        .flatMap { gc -> gc.grades!!.map { gc.interval!!.yearId to normalizeGrade(it.value) } }
                                                                        .filter { it.second != "N/A" }
                                                                        .toList()

                                                                    val groupedByYear = allGradesByYear
                                                                        .groupBy({ it.first }, { it.second })
                                                                        .mapValues { it.value.groupingBy { grade -> grade }.eachCount() }

                                                                    val sortedYears = groupedByYear.keys.sorted()
                                                                    val uniqueGrades = groupedByYear.values
                                                                        .flatMap { it.keys }
                                                                        .distinct()
                                                                        .sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }

                                                                    val barChartEntries = sortedYears.map { yearId ->
                                                                        val yearName = years.firstOrNull { it.id == yearId }?.name.orEmpty().removeRange(0,2).removeRange(3,5)
                                                                        val counts = groupedByYear[yearId] ?: emptyMap()
                                                                        object : VerticalBarPlotGroupedPointEntry<String, Float> {
                                                                            override val x = yearName.ifEmpty { yearId.toString() }
                                                                            override val y = uniqueGrades.map { grade ->
                                                                                DefaultVerticalBarPosition(0f, counts[grade]?.toFloat() ?: 0f)
                                                                            }
                                                                        }
                                                                    }

                                                                    val pivotedData = mutableMapOf<String, MutableMap<Int, Int>>()
                                                                    groupedByYear.forEach { (yearId, grades) ->
                                                                        grades.forEach { (grade, count) ->
                                                                            pivotedData.getOrPut(grade) { mutableMapOf() }[yearId] = count
                                                                        }
                                                                    }
                                                                    val sortedGrades = uniqueGrades
                                                                    val yearColors = generateHueColorPalette(sortedYears.size)

                                                                    val stackedEntries = sortedGrades.map { grade ->
                                                                        object : VerticalBarPlotStackedPointEntry<String, Float> {
                                                                            override val x = grade
                                                                            override val yOrigin = 0f
                                                                            override val y = pivotedData[grade]
                                                                                ?.let { counts ->
                                                                                    sortedYears.map { counts[it]?.toFloat() ?: 0f }
                                                                                        .scan(0f) { acc, v -> acc + v }
                                                                                        .drop(1)
                                                                                }
                                                                                ?: emptyList()
                                                                        }
                                                                    }

                                                                    val averageGrades = groupedByYear.mapNotNull { (yearId, counts) ->
                                                                        val total = counts.values.sum()
                                                                        if (total > 0) {
                                                                            val sum = counts.entries.sumOf { (g, c) -> (g.toIntOrNull() ?: 0) * c }
                                                                            yearId to (sum.toFloat() / total)
                                                                        } else null
                                                                    }.toMap()

                                                                    val avgPlot = averageGrades.keys.sorted().map { yearId ->
                                                                        val yearName = years.firstOrNull { it.id == yearId }?.name.orEmpty().removeRange(0,2).removeRange(3,5)
                                                                        DefaultPoint(yearName.ifEmpty { yearId.toString() }, averageGrades[yearId]!!)
                                                                    }

                                                                    val polarData = uniqueGrades.map { grade ->
                                                                        sortedYears.map { yearId ->
                                                                            val count = groupedByYear[yearId]?.get(grade)?.toFloat() ?: 0f
                                                                            val yearName = years.firstOrNull { it.id == yearId }?.name.orEmpty()
                                                                            DefaultPolarPoint(count, yearName.ifEmpty { yearId.toString() })
                                                                        }
                                                                    }

                                                                    mapOf(
                                                                        "grouped" to groupedByYear,
                                                                        "sortedYears" to sortedYears,
                                                                        "uniqueGrades" to uniqueGrades,
                                                                        "barEntries" to barChartEntries,
                                                                        "stackedEntries" to stackedEntries,
                                                                        "avgPlot" to avgPlot,
                                                                        "polarData" to polarData,
                                                                        "yearColors" to yearColors
                                                                    )
                                                                }

                                                                @Suppress("UNCHECKED_CAST")
                                                                LazyColumn(
                                                                    modifier = Modifier.hazeSource(hazeState),
                                                                    state = secondLazyListState,
                                                                    contentPadding = PaddingValues(top = titleHeight, bottom = closeBarHeight),
                                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                                ) {
                                                                    item {
                                                                        val grouped = processedData["grouped"] as Map<Int, Map<String, Int>>
                                                                        val uniqueGrades = processedData["uniqueGrades"] as List<String>
                                                                        val barEntries = processedData["barEntries"] as List<VerticalBarPlotGroupedPointEntry<String, Float>>
                                                                        val maxCount = grouped.values.maxOfOrNull { it.values.maxOrNull() ?: 0 }?.toFloat() ?: 1f

                                                                        val gradeColors = listOf(
                                                                            Color(0xFF4CAF50),
                                                                            Color(0xFF8BC34A),
                                                                            Color(0xFFCDDC39),
                                                                            Color(0xFFFFEB3B),
                                                                            Color(0xFFFF9800),
                                                                            Color(0xFFF44336)
                                                                        )

                                                                        ChartLayout(
                                                                            modifier = Modifier.padding(10.dp).height(400.dp),
                                                                            legend = {
                                                                                FlowLegend(
                                                                                    itemCount = uniqueGrades.size,
                                                                                    symbol = { i ->
                                                                                        Symbol(modifier = Modifier.size(12.dp).clip(CircleShape), fillBrush = SolidColor(gradeColors[i % gradeColors.size]))
                                                                                    },
                                                                                    label = { i -> Text("Note ${uniqueGrades[i]}") },
                                                                                    modifier = Modifier.padding(top = 16.dp)
                                                                                )
                                                                            },
                                                                            legendLocation = LegendLocation.BOTTOM
                                                                        ) {
                                                                            XYGraph(
                                                                                xAxisModel = CategoryAxisModel(categories = barEntries.map { it.x }),
                                                                                yAxisModel = FloatLinearAxisModel(
                                                                                    range = 0f..maxCount,
                                                                                    minimumMajorTickIncrement = 1f,
                                                                                    minorTickCount = 0
                                                                                ),
                                                                                xAxisLabels = { it },
                                                                                yAxisLabels = { it.toInt().toString() },
                                                                                xAxisTitle = "Jahr",
                                                                                yAxisTitle = "Anzahl"
                                                                            ) {
                                                                                GroupedVerticalBarPlot(
                                                                                    data = barEntries,
                                                                                    bar = { _, groupIndex, _ ->
                                                                                        DefaultVerticalBar(
                                                                                            brush = SolidColor(gradeColors[groupIndex % gradeColors.size]),
                                                                                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                                                                        )
                                                                                    },
                                                                                    animationSpec = KoalaPlotTheme.animationSpec
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                    item { Spacer(Modifier.height(20.dp)) }
                                                                    item {
                                                                        val stacked = processedData["stackedEntries"] as List<VerticalBarPlotStackedPointEntry<String, Float>>
                                                                        val sortedGrades = processedData["uniqueGrades"] as List<String>
                                                                        val sortedYears = processedData["sortedYears"] as List<Int>
                                                                        val yearColors = processedData["yearColors"] as List<Color>
                                                                        val maxY = stacked.maxOfOrNull { it.y.lastOrNull() ?: 0f } ?: 1f

                                                                        ChartLayout(
                                                                            modifier = Modifier.padding(10.dp).fillMaxWidth().height(400.dp),
                                                                            legend = {
                                                                                FlowLegend(
                                                                                    itemCount = sortedYears.size,
                                                                                    symbol = { i ->
                                                                                        Symbol(
                                                                                            modifier = Modifier.size(12.dp).clip(CircleShape),
                                                                                            fillBrush = SolidColor(yearColors[i])
                                                                                        )
                                                                                    },
                                                                                    label = { i -> Text(years.firstOrNull { it.id == sortedYears[i] }?.name.orEmpty()) },
                                                                                    modifier = Modifier.padding(top = 16.dp)
                                                                                )
                                                                            },
                                                                            legendLocation = LegendLocation.BOTTOM
                                                                        ) {
                                                                            XYGraph(
                                                                                xAxisModel = CategoryAxisModel(categories = sortedGrades),
                                                                                yAxisModel = FloatLinearAxisModel(range = 0f..maxY, minimumMajorTickIncrement = 5f),
                                                                                xAxisLabels = { it },
                                                                                yAxisLabels = { it.toInt().toString() },
                                                                                xAxisTitle = "Note",
                                                                                yAxisTitle = "Anzahl"
                                                                            ) {
                                                                                StackedVerticalBarPlot(
                                                                                    data = stacked,
                                                                                    bar = { _, barIndex ->
                                                                                        DefaultVerticalBar(
                                                                                            brush = SolidColor(yearColors[barIndex % yearColors.size]),
                                                                                            shape = RoundedCornerShape(8.dp)
                                                                                        )
                                                                                    }
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                    item { Spacer(Modifier.height(20.dp)) }
                                                                    item {
                                                                        val avgPlot = processedData["avgPlot"] as List<Point<String, Float>>
                                                                        if (avgPlot.isEmpty()) {
                                                                            Text("Nicht genügend Daten für die Durchschnittsanzeige vorhanden", modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
                                                                        } else {
                                                                            ChartLayout(modifier = Modifier.padding(10.dp).fillMaxWidth().height(400.dp)) {
                                                                                XYGraph(
                                                                                    xAxisModel = CategoryAxisModel(categories = avgPlot.map { it.x }),
                                                                                    yAxisModel = FloatLinearAxisModel(range = 0.5f..6.5f),
                                                                                    xAxisLabels = { it },
                                                                                    yAxisLabels = { it.toString(0) },
                                                                                    xAxisTitle = "Jahr",
                                                                                    yAxisTitle = "Durchschnittsnote"
                                                                                ) {
                                                                                    AreaPlot(
                                                                                        data = avgPlot,
                                                                                        lineStyle = LineStyle(brush = SolidColor(colorScheme.primary), strokeWidth = 3.dp),
                                                                                        areaStyle = AreaStyle(
                                                                                            brush = SolidColor(colorScheme.primary.copy(alpha = 0.5f)),
                                                                                            alpha = 0.5f
                                                                                        ),
                                                                                        areaBaseline = AreaBaseline.ConstantLine(0.5f)
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if (years.size > 2) {
                                                                        item {
                                                                            val polarData = processedData["polarData"] as List<List<PolarPoint<Float, String>>>
                                                                            val sortedYears = processedData["sortedYears"] as List<Int>
                                                                            val uniqueGrades = processedData["uniqueGrades"] as List<String>
                                                                            val radialMax = polarData.flatten().maxOfOrNull { it.r } ?: 0f
                                                                            val tick = when {
                                                                                radialMax <= 20f -> 5
                                                                                radialMax <= 50f -> 10
                                                                                radialMax <= 100f -> 20
                                                                                else -> 25
                                                                            }
                                                                            val radialMaxRounded = ceil(radialMax / tick) * tick
                                                                            val ticks = (0..radialMaxRounded.toInt() step tick).map { it.toFloat() }

                                                                            val gradeColors = listOf(
                                                                                Color(0xFF4CAF50),
                                                                                Color(0xFF8BC34A),
                                                                                Color(0xFFCDDC39),
                                                                                Color(0xFFFFEB3B),
                                                                                Color(0xFFFF9800),
                                                                                Color(0xFFF44336)
                                                                            )

                                                                            ChartLayout(
                                                                                modifier = Modifier.padding(10.dp).fillMaxWidth().aspectRatio(1f),
                                                                                legend = {
                                                                                    FlowLegend(
                                                                                        itemCount = uniqueGrades.size,
                                                                                        symbol = { i ->
                                                                                            Symbol(
                                                                                                modifier = Modifier.size(12.dp).clip(CircleShape),
                                                                                                fillBrush = SolidColor(gradeColors[i])
                                                                                            )
                                                                                        },
                                                                                        label = { Text("Note ${uniqueGrades[it]}") }
                                                                                    )
                                                                                },
                                                                                legendLocation = LegendLocation.BOTTOM
                                                                            ) {
                                                                                PolarGraph(
                                                                                    radialAxisModel = rememberFloatRadialAxisModel(tickValues = ticks),
                                                                                    angularAxisModel = rememberCategoryAngularAxisModel(categories = sortedYears.map { years.firstOrNull { y -> y.id == it }?.name.orEmpty() }),
                                                                                    radialAxisLabels = { Text(it.toInt().toString()) },
                                                                                    angularAxisLabels = { Text(it) }
                                                                                ) {
                                                                                    polarData.forEachIndexed { index, series ->
                                                                                        PolarPlotSeries(
                                                                                            data = series,
                                                                                            lineStyle = LineStyle(SolidColor(gradeColors[index]), strokeWidth = 2.dp),
                                                                                            areaStyle = AreaStyle(SolidColor(gradeColors[index]), alpha = 0.3f),
                                                                                            symbols = { Symbol(shape = CircleShape, fillBrush = SolidColor(gradeColors[index])) }
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
                                            }

                                            Column(Modifier
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState())
                                                .align(Alignment.TopCenter)
                                                .enhancedHazeEffect(hazeState, colorScheme.surfaceContainerHighest) {
                                                    if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) blurEnabled = false
                                                    progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                                                }
                                                .onGloballyPositioned {
                                                    titleHeight = with(density) { it.size.height.toDp() }
                                                }
                                            ) {
                                                Text(
                                                    text = "Noten analysieren/vergleichen",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(15.dp)
                                                        .align(Alignment.CenterHorizontally),
                                                    color = colorScheme.onSurface,
                                                    style = typography.headlineSmall,
                                                    textAlign = TextAlign.Center
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    EnhancedCheckbox(
                                                        checked = analyzeYears,
                                                        onCheckedChange = {
                                                            scope.launch {
                                                                analyzeYears = it
                                                                if (it) {
                                                                    if (!viewModel.allGradeCollectionsLoaded.value) {
                                                                        isLoading = true
                                                                        viewModel.gradeCollections.clear()
                                                                        viewModel.getCollections(viewModel.years)?.let {
                                                                            viewModel.gradeCollections.addAll(it)
                                                                            viewModel.allGradeCollectionsLoaded.value = true
                                                                            isLoading = false
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        enabled = !isLoading && viewModel.years.size > 1
                                                    )
                                                    Text(
                                                        text = "Jahre analysieren/vergleichen",
                                                        style = typography.bodyLarge
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    EnhancedCheckbox(
                                                        checked = filterSubjects,
                                                        onCheckedChange = { filterSubjects = it },
                                                        enabled = !isLoading && viewModel.years.size > 1
                                                    )
                                                    Text(
                                                        text = "Fächer filtern",
                                                        modifier = Modifier.weight(1f),
                                                        style = typography.bodyLarge
                                                    )
                                                    AnimatedVisibility(filterSubjects) {
                                                        EnhancedIconButton(
                                                            onClick = {
                                                                filterShown = !filterShown
                                                            }
                                                        ) {
                                                            AnimatedContent(filterShown) {
                                                                if (it) {
                                                                    Icon(Icons.Outlined.Remove, null)
                                                                } else {
                                                                    Icon(Icons.Outlined.Add, null)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                AnimatedVisibility(filterSubjects && filterShown) {
                                                    FlowRow(
                                                        modifier = Modifier.padding(horizontal = 15.dp).padding(bottom = closeBarHeight),
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        (if (analyzeYears) allFilteredGrades.toList() else filteredGrades)
                                                            .map { it.subject?.name ?: "" }.toSet().forEach { subject ->
                                                            FilterChip(
                                                                selected = !deselectedSubjects.contains(subject),
                                                                onClick = {
                                                                    if (deselectedSubjects.contains(subject)) {
                                                                        deselectedSubjects.remove(subject)
                                                                    } else {
                                                                        deselectedSubjects.add(subject)
                                                                    }
                                                                },
                                                                label = { Text(subject) }
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Box(Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .enhancedHazeEffect(hazeState, colorScheme.surfaceContainerHighest) {
                                                    if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) blurEnabled = false
                                                    progressive = HazeProgressive.verticalGradient()
                                                }
                                                .onGloballyPositioned {
                                                    closeBarHeight = with(density) { it.size.height.toDp() }
                                                }
                                            ) {
                                                EnhancedButton(
                                                    onClick = {
                                                        scope.launch {
                                                            state = 0
                                                            contentBlurred = false
                                                            delay(250)
                                                            if (state == 0) userScrollEnabled = true
                                                        }
                                                    },
                                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterEnd)
                                                ) {
                                                    Text("Schließen")
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
        }
    }
}