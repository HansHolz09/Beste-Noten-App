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
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.isScrollingUp
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class
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
                    viewModel.years.addAll(viewModel.api.getYears().data)
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
                                                        .filter { it.name.contains(searchQuery, true) }
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
                                                                            Text("${if (showTeachersWithFirstname) it.conductor.forename else it.conductor.forename?.take(1) + "."} ${it.conductor.name}: ${it.body}")
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
                                                    .filter { it.name.contains(searchQuery, true) }
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
                                                                                    Text("${if (showTeachersWithFirstname) it.conductor.forename else it.conductor.forename?.take(1) + "."} ${it.conductor.name}: ${it.body}")
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
                            modifier = Modifier.onGloballyPositioned {
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
                                                                }
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
                                            Text("TODO", Modifier.fillMaxWidth().height(200.dp))
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        state = 0
                                                        contentBlurred = false
                                                        delay(1000)
                                                        if (state == 0) userScrollEnabled = true
                                                    }
                                                }
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
        )
    }
}