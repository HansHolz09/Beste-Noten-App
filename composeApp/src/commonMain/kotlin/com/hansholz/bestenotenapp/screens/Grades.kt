@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.api.Year
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.LocalTitleBarModifier
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.customTitleBarMouseEventHandler
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.isScrollingUp
import com.hansholz.bestenotenapp.utils.normalizeGrade
import com.hansholz.bestenotenapp.utils.topAppBarPadding
import com.hansholz.bestenotenapp.utils.translateHistoryBody
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.bar.*
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.AreaBaseline
import io.github.koalaplot.core.line.AreaPlot
import io.github.koalaplot.core.polar.*
import io.github.koalaplot.core.style.AreaStyle
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.toString
import io.github.koalaplot.core.xygraph.*
import kotlinx.coroutines.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
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
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        var showGradeHistory by LocalShowGradeHistory.current
        var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
        var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
        val settings = Settings()

        var isLoading by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        val selectedYears = remember { mutableStateListOf<Year>() }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                isLoading = true
                if (viewModel.years.isEmpty()) {
                    viewModel.years.addAll(viewModel.api.yearIndex().data)
                }
                if (viewModel.gradeCollections.isEmpty()) {
                    viewModel.gradeCollections.addAll(viewModel.getCollections(listOf(viewModel.years.last())))
                }
                if (viewModel.years.isNotEmpty()) {
                    selectedYears.clear()
                    selectedYears.add(viewModel.years.last())
                }
                isLoading = false
            }
        }

        Scaffold(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "grades-card"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Noten",
                            modifier = Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "grades-title"),
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
                Box(Modifier.fillMaxSize()) {
                    var topPadding by remember { mutableStateOf(0.dp) }
                    var toolbarPadding by remember { mutableStateOf(0.dp) }
                    val toolbarContentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())
                    val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding() + toolbarPadding)

                    val pagerState = rememberPagerState { 2 }
                    var userScrollEnabled by remember { mutableStateOf(true) }
                    var contentBlurred by remember { mutableStateOf(false) }
                    val contentBlurRadius = animateDpAsState(
                        targetValue = if (contentBlurred) 20.dp else 0.dp,
                        animationSpec = tween(1000)
                    )
                    val firstLazyListState = rememberLazyListState()
                    val secondLazyListState = rememberLazyListState()
                    val currentLazyListState = when(pagerState.currentPage) {
                        0 -> firstLazyListState
                        1 -> secondLazyListState
                        else -> rememberLazyListState()
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.hazeSource(viewModel.hazeBackgroundState).enhancedHazeEffect { blurRadius = contentBlurRadius.value },
                        userScrollEnabled = userScrollEnabled
                    ) {
                        AnimatedContent(isLoading) { targetState ->
                            Box(Modifier.fillMaxSize()) {
                                if (targetState) {
                                    ContainedLoadingIndicator(Modifier.padding(contentPadding).align(Alignment.Center))
                                } else {
                                    when(it) {
                                        0 -> {
                                            LazyColumn(
                                                state = firstLazyListState,
                                                contentPadding = contentPadding,
                                                userScrollEnabled = userScrollEnabled
                                            ) {
                                                items(
                                                    viewModel
                                                        .gradeCollections
                                                        .toSet()
                                                        .filter { selectedYears.map { it.id }.contains(it.interval?.yearId) }
                                                        .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
                                                        .filter { (it.name ?: "").contains(searchQuery, true) }
                                                        .sortedByDescending { it.givenAt }
                                                        .toList()
                                                ) {
                                                    EnhancedAnimated(
                                                        preset = ZoomIn(),
                                                        durationMillis = 200,
                                                    ) {
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
                                                                        histories.forEach {
                                                                            Text("${if (showTeachersWithFirstname) it.conductor?.forename else it.conductor?.forename?.take(1) + "."} ${it.conductor?.name}: ${translateHistoryBody(it.body)}")
                                                                        }
                                                                    }
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
                                        1 -> {
                                            LazyColumn(
                                                state = secondLazyListState,
                                                modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
                                                contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                                                userScrollEnabled = userScrollEnabled
                                            ) {
                                                viewModel.gradeCollections
                                                    .toSet()
                                                    .filter { selectedYears.map { it.id }.contains(it.interval?.yearId) }
                                                    .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
                                                    .filter { (it.name ?: "").contains(searchQuery, true) }
                                                    .sortedWith(compareBy({ it.subject?.name }, { it.givenAt }))
                                                    .groupBy { it.subject?.name }
                                                    .toList()
                                                    .forEachIndexed { secIdx, (title, items) ->
                                                        stickyHeader {
                                                            EnhancedAnimated(
                                                                preset = ZoomIn(),
                                                                durationMillis = 200,
                                                            ) {
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
                                                                                .padding(start = 16.dp)
                                                                        )
                                                                    }
                                                                    HorizontalDivider(thickness = 1.dp)
                                                                }
                                                            }
                                                        }
                                                        items(items) {
                                                            EnhancedAnimated(
                                                                preset = ZoomIn(),
                                                                durationMillis = 200,
                                                            ) {
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
                                                                                histories.forEach {
                                                                                    Text("${if (showTeachersWithFirstname) it.conductor?.forename else it.conductor?.forename?.take(1) + "."} ${it.conductor?.name}: ${translateHistoryBody(it.body)}")
                                                                                }
                                                                            }
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
                                    delay(1000)
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
                                fadeIn(animationSpec = tween(250)) with
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
                                                .sharedBounds(
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    }
                                                )
                                                .clip(FloatingToolbarDefaults.ContainerShape).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(Color.Transparent),
                                            leadingContent = {
                                                IconButton(
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
                                                IconButton(
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
                                                IconButton(
                                                    onClick = {
                                                        scope.launch {
                                                            isLoading = true
                                                            viewModel.gradeCollections.clear()
                                                            if (viewModel.allGradeCollectionsLoaded.value) {
                                                                viewModel.gradeCollections.addAll(viewModel.getCollections(viewModel.years))
                                                            } else {
                                                                viewModel.gradeCollections.addAll(viewModel.getCollections(listOf(viewModel.years.last())))
                                                            }
                                                            isLoading = false
                                                        }
                                                    },
                                                    enabled = !isLoading
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Refresh,
                                                        contentDescription = null,
                                                        tint = colorScheme.onPrimaryContainer
                                                    )
                                                }
                                                IconButton(
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
                                            FilledIconButton(
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
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.BarChart,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimary
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
                                                .sharedBounds(
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    }
                                                )
                                                .then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(12.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
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
                                                IconButton(
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
                                                IconButton(
                                                    onClick = {
                                                        scope.launch {
                                                            searchQuery = ""
                                                            state = 0
                                                            userScrollEnabled = false
                                                            delay(1000)
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
                                                .sharedBounds(
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    }
                                                )
                                                .then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(12.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                            colors = CardDefaults.cardColors(Color.Transparent)
                                        ) {
                                            LaunchedEffect(Unit) {
                                                if (!viewModel.allGradeCollectionsLoaded.value) {
                                                    isLoading = true
                                                    viewModel.gradeCollections.clear()
                                                    viewModel.gradeCollections.addAll(viewModel.getCollections(viewModel.years))
                                                    viewModel.allGradeCollectionsLoaded.value = true
                                                    isLoading = false
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
                                                            Checkbox(
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
                                                                fontSize = 18.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        state = 0
                                                        contentBlurred = false
                                                        delay(1000)
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
                                                .sharedBounds(
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    }
                                                )
                                                .then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(12.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
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
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
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
                                                    settingsToggleItem(
                                                        checked = showTeachersWithFirstname,
                                                        onCheckedChange = {
                                                            showTeachersWithFirstname = it
                                                            settings["showTeachersWithFirstname"] = it
                                                        },
                                                        text = "Lehrer mit Vornamen anzeigen"
                                                    )
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        state = 0
                                                        contentBlurred = false
                                                        delay(1000)
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
                                                .sharedBounds(
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    }
                                                )
                                                .then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 600.dp)
                                                .clip(RoundedCornerShape(12.dp)).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.surfaceContainerHighest),
                                            colors = CardDefaults.cardColors(Color.Transparent)
                                        ) {
                                            Box {
                                                var analyzeYears by remember { mutableStateOf(false) }
                                                var titleHeight by remember { mutableStateOf(0.dp) }
                                                var closeBarHeight by remember { mutableStateOf(0.dp) }
                                                val lazyListState = rememberLazyListState()

                                                AnimatedContent(analyzeYears) { analyzeYears ->
                                                    if (!analyzeYears) {
                                                        LazyColumn(
                                                            modifier = Modifier.hazeSource(hazeState),
                                                            state = lazyListState,
                                                            contentPadding = PaddingValues(top = titleHeight, bottom = closeBarHeight),
                                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            item {
                                                                val grades = viewModel.gradeCollections
                                                                    .toSet().filter { selectedYears.map { it.id }.contains(it.interval?.yearId) }
                                                                    .filter { !it.grades.isNullOrEmpty() && it.grades.firstOrNull()?.value?.take(1)?.toIntOrNull() != null }
                                                                    .map { it.grades!![0].value.take(1).toIntOrNull() ?: 0 }.sortedBy { it }.groupBy { it }.toList()
                                                                val barChartEntries = buildList {
                                                                    grades.map { (int, grade) ->
                                                                        add(DefaultVerticalBarPlotEntry(int.toFloat(), DefaultVerticalBarPosition(0f, grade.size.toFloat())))
                                                                    }
                                                                }
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
                                                            }
                                                        }
                                                    } else {
                                                        AnimatedContent(isLoading) { targetState ->
                                                            Box(Modifier.fillMaxWidth().sizeIn(minHeight = 300.dp)) {
                                                                if (targetState) {
                                                                    ContainedLoadingIndicator(Modifier.align(Alignment.Center).padding(top = titleHeight, bottom = closeBarHeight))
                                                                } else {
                                                                    LazyColumn(
                                                                        modifier = Modifier.hazeSource(hazeState),
                                                                        state = lazyListState,
                                                                        contentPadding = PaddingValues(top = titleHeight, bottom = closeBarHeight),
                                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                                    ) {
                                                                        val allGradesByYear: List<Pair<Int, String>> = viewModel.gradeCollections
                                                                            .toSet()
                                                                            .filter { it.interval?.yearId != null && !it.grades.isNullOrEmpty() }
                                                                            .flatMap { gradeCollection ->
                                                                                gradeCollection.grades!!.map { grade ->
                                                                                    (gradeCollection.interval!!.yearId) to normalizeGrade(grade.value)
                                                                                }
                                                                            }
                                                                            .filter { it.second != "N/A" }

                                                                        val groupedData: Map<Int, Map<String, Int>> = allGradesByYear
                                                                            .groupBy { it.first }
                                                                            .mapValues { entry ->
                                                                                entry.value
                                                                                    .map { it.second }
                                                                                    .groupingBy { it }
                                                                                    .eachCount()
                                                                            }

                                                                        item {
                                                                            val uniqueGrades: List<String> = groupedData.values
                                                                                .flatMap { it.keys }
                                                                                .distinct()
                                                                                .sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }

                                                                            val sortedYears: List<Int> = groupedData.keys.sorted()

                                                                            val barChartEntries = sortedYears.map { yearId ->
                                                                                val yearName = viewModel.years.firstOrNull { it.id == yearId }?.name ?: yearId.toString()
                                                                                val gradesForYear = groupedData[yearId] ?: emptyMap()

                                                                                object : VerticalBarPlotGroupedPointEntry<String, Float> {
                                                                                    override val x: String = yearName

                                                                                    override val y: List<VerticalBarPosition<Float>> = uniqueGrades.map { grade ->
                                                                                        val count = gradesForYear[grade]?.toFloat() ?: 0f
                                                                                        DefaultVerticalBarPosition(0f, count)
                                                                                    }
                                                                                }
                                                                            }

                                                                            val allYearNames = barChartEntries.map { it.x }
                                                                            val maxCount = groupedData.values
                                                                                .maxOfOrNull { it.values.maxOrNull() ?: 0 }?.toFloat() ?: 1f

                                                                            val xAxisModel = CategoryAxisModel(categories = allYearNames)
                                                                            val yAxisModel = FloatLinearAxisModel(
                                                                                range = 0f..maxCount,
                                                                                minimumMajorTickIncrement = 1f,
                                                                                minorTickCount = 0
                                                                            )

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
                                                                                        label = { i ->
                                                                                            Text("Note $i")
                                                                                        },
                                                                                        modifier = Modifier.padding(top = 16.dp)
                                                                                    )
                                                                                },
                                                                                legendLocation = LegendLocation.BOTTOM
                                                                            ) {
                                                                                XYGraph(
                                                                                    xAxisModel = xAxisModel,
                                                                                    yAxisModel = yAxisModel,
                                                                                    xAxisLabels = { it },
                                                                                    yAxisLabels = { it.toInt().toString() },
                                                                                    xAxisTitle = "Jahr",
                                                                                    yAxisTitle = "Anzahl",
                                                                                ) {
                                                                                    GroupedVerticalBarPlot(
                                                                                        data = barChartEntries,
                                                                                        bar = { dataIndex, groupIndex, value ->
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

                                                                        item {
                                                                            Spacer(Modifier.height(20.dp))
                                                                        }

                                                                        item {
                                                                            val pivotedData = mutableMapOf<String, MutableMap<Int, Int>>()
                                                                            groupedData.forEach { (yearId, gradesMap) ->
                                                                                gradesMap.forEach { (grade, count) ->
                                                                                    pivotedData.getOrPut(grade) { mutableMapOf() }[yearId] = count
                                                                                }
                                                                            }

                                                                            val sortedGrades = pivotedData.keys.sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }

                                                                            val sortedYears = groupedData.keys.sorted()

                                                                            val yearColors = generateHueColorPalette(sortedYears.size)

                                                                            val stackedBarEntries = sortedGrades.map { grade ->
                                                                                object : VerticalBarPlotStackedPointEntry<String, Float> {
                                                                                    override val x: String = grade

                                                                                    override val yOrigin: Float = 0f

                                                                                    override val y: List<Float> =
                                                                                        pivotedData[grade]?.let { countsByYear ->
                                                                                            sortedYears.map { yearId -> countsByYear[yearId]?.toFloat() ?: 0f }
                                                                                        }?.scan(0f) { accumulator, value -> accumulator + value }?.drop(1) ?: emptyList()
                                                                                }
                                                                            }

                                                                            val maxYValue = stackedBarEntries.maxOfOrNull { it.y.lastOrNull() ?: 0f } ?: 1f

                                                                            ChartLayout(
                                                                                modifier = Modifier.padding(10.dp).fillMaxWidth().height(400.dp),
                                                                                legend = {
                                                                                    FlowLegend(
                                                                                        itemCount = sortedYears.size,
                                                                                        symbol = { i ->
                                                                                            Symbol(modifier = Modifier.size(12.dp).clip(CircleShape), fillBrush = SolidColor(yearColors[i]))
                                                                                        },
                                                                                        label = { i ->
                                                                                            val yearName = viewModel.years.firstOrNull { it.id == sortedYears[i] }?.name ?: sortedYears[i].toString()
                                                                                            Text(yearName)
                                                                                        },
                                                                                        modifier = Modifier.padding(top = 16.dp)
                                                                                    )
                                                                                },
                                                                                legendLocation = LegendLocation.BOTTOM
                                                                            ) {
                                                                                XYGraph(
                                                                                    xAxisModel = CategoryAxisModel(categories = sortedGrades),
                                                                                    yAxisModel = FloatLinearAxisModel(range = 0f..maxYValue, minimumMajorTickIncrement = 5f),
                                                                                    xAxisLabels = { it },
                                                                                    yAxisLabels = { it.toInt().toString() },
                                                                                    xAxisTitle = "Note",
                                                                                    yAxisTitle = "Anzahl",
                                                                                ) {
                                                                                    StackedVerticalBarPlot(
                                                                                        data = stackedBarEntries,
                                                                                        bar = { xIndex, barIndex ->
                                                                                            DefaultVerticalBar(
                                                                                                brush = SolidColor(yearColors[barIndex % yearColors.size]),
                                                                                                shape = RoundedCornerShape(8.dp)
                                                                                            )
                                                                                        }
                                                                                    )
                                                                                }
                                                                            }
                                                                        }

                                                                        if (viewModel.years.size > 2) {
                                                                            item {
                                                                                Spacer(Modifier.height(20.dp))
                                                                            }

                                                                            item {
                                                                                val chartData = remember(viewModel.gradeCollections, viewModel.years) {
                                                                                    val groupedData = viewModel.gradeCollections.asSequence()
                                                                                        .filter { it.interval?.yearId != null && !it.grades.isNullOrEmpty() }
                                                                                        .flatMap { gc -> gc.grades!!.map { grade -> gc.interval!!.yearId to normalizeGrade(grade.value) } }
                                                                                        .filter { it.second != "N/A" }
                                                                                        .groupBy({ it.first }) { it.second }
                                                                                        .mapValues { entry -> entry.value.groupingBy { it }.eachCount() }

                                                                                    val sortedGrades = groupedData.values.flatMap { it.keys }.distinct().sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }
                                                                                    val sortedYears = groupedData.keys.sorted()
                                                                                    val yearNames = sortedYears.map { yearId -> viewModel.years.firstOrNull { it.id == yearId }?.name ?: yearId.toString() }
                                                                                    val gradeColors = generateHueColorPalette(sortedGrades.size)

                                                                                    val polarData = sortedGrades.map { grade ->
                                                                                        sortedYears.map { yearId ->
                                                                                            val count = groupedData[yearId]?.get(grade)?.toFloat() ?: 0f
                                                                                            val yearName = viewModel.years.firstOrNull { it.id == yearId }?.name ?: yearId.toString()
                                                                                            DefaultPolarPoint(count, yearName)
                                                                                        }
                                                                                    }

                                                                                    val maxCount = polarData.flatten().maxOfOrNull { it.r } ?: 0f

                                                                                    val tickStep = when {
                                                                                        maxCount <= 20 -> 5
                                                                                        maxCount <= 50 -> 10
                                                                                        maxCount <= 100 -> 20
                                                                                        else -> 25
                                                                                    }

                                                                                    val radialAxisMax = (ceil(maxCount / tickStep) * tickStep).coerceAtLeast(10f)
                                                                                    val radialAxisTicks = (0..radialAxisMax.toInt() step tickStep).map { it.toFloat() }

                                                                                    object {
                                                                                        val data = polarData
                                                                                        val angularAxisCategories = yearNames
                                                                                        val radialAxisTicks = radialAxisTicks
                                                                                        val legendItems = sortedGrades.mapIndexed { index, grade -> "Note $grade" to gradeColors[index] }
                                                                                    }
                                                                                }

                                                                                ChartLayout(
                                                                                    modifier = Modifier.padding(10.dp).fillMaxWidth().aspectRatio(1f),
                                                                                    legend = {
                                                                                        FlowLegend(
                                                                                            itemCount = chartData.legendItems.size,
                                                                                            symbol = { i ->
                                                                                                Symbol(modifier = Modifier.size(12.dp).clip(CircleShape), fillBrush = SolidColor(chartData.legendItems[i].second))
                                                                                            },
                                                                                            label = { i -> Text(chartData.legendItems[i].first) }
                                                                                        )
                                                                                    },
                                                                                    legendLocation = LegendLocation.BOTTOM
                                                                                ) {
                                                                                    PolarGraph(
                                                                                        radialAxisModel = rememberFloatRadialAxisModel(tickValues = chartData.radialAxisTicks),
                                                                                        angularAxisModel = rememberCategoryAngularAxisModel(categories = chartData.angularAxisCategories),
                                                                                        radialAxisLabels = { Text(it.toInt().toString()) },
                                                                                        angularAxisLabels = { Text(it) },
                                                                                        polarGraphProperties = PolarGraphDefaults.PolarGraphPropertyDefaults()
                                                                                    ) {
                                                                                        chartData.data.forEachIndexed { index, seriesData ->
                                                                                            val color = chartData.legendItems[index].second
                                                                                            PolarPlotSeries(
                                                                                                data = seriesData,
                                                                                                lineStyle = LineStyle(SolidColor(color), strokeWidth = 2.dp),
                                                                                                areaStyle = AreaStyle(SolidColor(color), alpha = 0.3f),
                                                                                                symbols = { Symbol(shape = CircleShape, fillBrush = SolidColor(color)) }
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }


                                                                        item {
                                                                            Spacer(Modifier.height(20.dp))
                                                                        }

                                                                        item {
                                                                            val chartData = remember(viewModel.gradeCollections, viewModel.years) {
                                                                                val groupedData = viewModel.gradeCollections.asSequence()
                                                                                    .filter { it.interval?.yearId != null && !it.grades.isNullOrEmpty() }
                                                                                    .flatMap { gc -> gc.grades!!.map { grade -> gc.interval!!.yearId to normalizeGrade(grade.value) } }
                                                                                    .filter { it.second != "N/A" && it.second.toIntOrNull() != null }
                                                                                    .groupBy({ it.first }) { it.second }
                                                                                    .mapValues { entry -> entry.value.groupingBy { it }.eachCount() }

                                                                                val averageGrades: Map<Int, Float> = groupedData.mapNotNull { (yearId, gradesMap) ->
                                                                                    val totalCount = gradesMap.values.sum()
                                                                                    if (totalCount == 0) {
                                                                                        null
                                                                                    } else {
                                                                                        val weightedSum = gradesMap.entries.sumOf { (grade, count) ->
                                                                                            (grade.toIntOrNull() ?: 0) * count
                                                                                        }
                                                                                        val average = weightedSum.toFloat() / totalCount.toFloat()
                                                                                        yearId to average
                                                                                    }
                                                                                }.toMap()

                                                                                val sortedYears = averageGrades.keys.sorted()

                                                                                val plotData: List<Point<String, Float>> = sortedYears.map { yearId ->
                                                                                    val yearName = viewModel.years.firstOrNull { it.id == yearId }?.name ?: yearId.toString()
                                                                                    DefaultPoint(yearName, averageGrades[yearId]!!)
                                                                                }

                                                                                val yearNames = plotData.map { it.x }
                                                                                val yAxisRange = 0.5f..6.5f

                                                                                object {
                                                                                    val data = plotData
                                                                                    val yearCategories = yearNames
                                                                                    val yAxisRange = yAxisRange
                                                                                }
                                                                            }

                                                                            if (chartData.data.isEmpty()) {
                                                                                Text("Nicht genügend Daten für die Durchschnittsanzeige vorhanden.", modifier = Modifier.padding(16.dp))
                                                                            }

                                                                            ChartLayout(modifier = Modifier.padding(10.dp).fillMaxWidth().height(400.dp)) {
                                                                                XYGraph(
                                                                                    xAxisModel = CategoryAxisModel(categories = chartData.yearCategories),
                                                                                    yAxisModel = FloatLinearAxisModel(range = chartData.yAxisRange),
                                                                                    xAxisLabels = { it },
                                                                                    yAxisLabels = { it.toString(0) },
                                                                                    xAxisTitle = "Jahr",
                                                                                    yAxisTitle = "Durchschnittsnote"
                                                                                ) {
                                                                                    AreaPlot(
                                                                                        data = chartData.data,
                                                                                        lineStyle = LineStyle(brush = SolidColor(colorScheme.primary), strokeWidth = 3.dp),
                                                                                        areaStyle = AreaStyle(
                                                                                            brush = SolidColor(colorScheme.primary.copy(0.5f)),
                                                                                            alpha = 0.5f,
                                                                                        ),
                                                                                        areaBaseline = AreaBaseline.ConstantLine(chartData.yAxisRange.start)
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

                                                Column(Modifier
                                                    .fillMaxWidth()
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
                                                        Checkbox(
                                                            checked = analyzeYears,
                                                            onCheckedChange = {
                                                                scope.launch {
                                                                    analyzeYears = it
                                                                    if (it) {
                                                                        if (!viewModel.allGradeCollectionsLoaded.value) {
                                                                            isLoading = true
                                                                            viewModel.gradeCollections.clear()
                                                                            viewModel.gradeCollections.addAll(viewModel.getCollections(viewModel.years))
                                                                            viewModel.allGradeCollectionsLoaded.value = true
                                                                            isLoading = false
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            enabled = !isLoading
                                                        )
                                                        Text(
                                                            text = "Jahre analysieren/vergleichen",
                                                            fontSize = 18.sp
                                                        )
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
                                                    Button(
                                                        onClick = {
                                                            scope.launch {
                                                                state = 0
                                                                contentBlurred = false
                                                                delay(1000)
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
        )
    }
}