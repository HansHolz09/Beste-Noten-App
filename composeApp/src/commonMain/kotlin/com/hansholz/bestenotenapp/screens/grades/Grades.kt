package com.hansholz.bestenotenapp.screens.grades

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DisabledVisible
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PlaylistRemove
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.GradeValueBox
import com.hansholz.bestenotenapp.components.PreferencePosition
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrate
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedPagerState
import com.hansholz.bestenotenapp.components.icons.MathAvg
import com.hansholz.bestenotenapp.components.rememberLazyListScrollSpeedState
import com.hansholz.bestenotenapp.components.settingsToggleItem
import com.hansholz.bestenotenapp.main.LocalGradeAverageEnabled
import com.hansholz.bestenotenapp.main.LocalGradeAverageUseWeighting
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.security.kSafe
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.utils.filterHistory
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.isScrollingUp
import com.hansholz.bestenotenapp.utils.translateHistoryBody
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import dev.chrisbanes.haze.hazeSource
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.math.abs

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalKoalaPlotApi::class,
)
@Composable
fun Grades(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val gradesViewModel = viewModel { GradesViewModel(viewModel) }

        val scope = rememberCoroutineScope()
        val vibrator = rememberVibrator()
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        @Suppress("DEPRECATION")
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val animationsEnabled by LocalAnimationsEnabled.current
        var gradeAverageEnabled by LocalGradeAverageEnabled.current
        var gradeAverageUseWeighting by LocalGradeAverageUseWeighting.current
        var showGradeHistory by LocalShowGradeHistory.current
        var showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
        var showTeachersWithFirstname by LocalShowTeachersWithFirstname.current
        val kSafe = remember { kSafe() }

        val subjectWeightings = remember { mutableStateMapOf<String, SubjectWeightingConfig>() }
        var weightingDialogState by remember { mutableStateOf<SubjectWeightingDialogState?>(null) }
        var weightingDialogVisible by remember { mutableStateOf(false) }

        TopAppBarScaffold(
            modifier =
                Modifier.enhancedSharedBounds(
                    sharedTransitionScope = sharedTransitionScope,
                    sharedContentState = rememberSharedContentState(key = "grades-card"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            title = "Noten",
            titleModifier =
                Modifier
                    .enhancedSharedElement(
                        sharedTransitionScope = sharedTransitionScope,
                        sharedContentState = rememberSharedContentState(key = "grades-title"),
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
            Box(Modifier.fillMaxSize()) {
                val toolbarContentPadding = PaddingValues(top = gradesViewModel.topPadding, bottom = innerPadding.calculateBottomPadding())
                val contentPadding = PaddingValues(top = gradesViewModel.topPadding, bottom = innerPadding.calculateBottomPadding() + gradesViewModel.toolbarPadding)
                val verticalPadding = PaddingValues(start = innerPadding.calculateStartPadding(layoutDirection), end = innerPadding.calculateEndPadding(layoutDirection))

                val pagerState = rememberEnhancedPagerState(2)
                val contentBlurRadius = animateDpAsState(if (gradesViewModel.contentBlurred) 10.dp else 0.dp)
                val firstLazyListState = rememberLazyListState()
                val secondLazyListState = rememberLazyListState()
                val currentLazyListState =
                    when (pagerState.currentPage) {
                        0 -> firstLazyListState
                        1 -> secondLazyListState
                        else -> rememberLazyListState()
                    }
                val items =
                    remember(
                        viewModel.gradeCollections,
                        gradesViewModel.selectedYears.size,
                        showCollectionsWithoutGrades,
                        gradesViewModel.searchQuery,
                        gradesViewModel.isLoading,
                    ) {
                        viewModel
                            .gradeCollections
                            .toSet()
                            .filter { gradesViewModel.selectedYears.map { it.id }.contains(it.interval?.yearId) }
                            .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
                            .filter { (it.name ?: "").contains(gradesViewModel.searchQuery, true) }
                    }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.hazeSource(viewModel.hazeBackgroundState).enhancedHazeEffect(blurRadius = contentBlurRadius.value),
                    userScrollEnabled = gradesViewModel.userScrollEnabled,
                ) { currentPage ->
                    EnhancedAnimatedContent(gradesViewModel.isLoading || items.isEmpty()) { targetState ->
                        Box(Modifier.fillMaxSize()) {
                            if (targetState) {
                                EnhancedAnimatedContent(gradesViewModel.isLoading) { isLoading ->
                                    if (isLoading) {
                                        Box(
                                            modifier = Modifier.padding(contentPadding).fillMaxSize(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            ContainedLoadingIndicator()
                                        }
                                    } else {
                                        EmptyStateMessage(
                                            title = if (gradesViewModel.searchQuery.isEmpty()) "Keine Noten vorhanden" else "Keine Noten gefunden",
                                            icon = if (gradesViewModel.searchQuery.isEmpty()) Icons.Outlined.PlaylistRemove else Icons.Outlined.SearchOff,
                                            modifier = Modifier.padding(contentPadding).consumeWindowInsets(contentPadding).imePadding(),
                                        )
                                    }
                                }
                            } else {
                                when (currentPage) {
                                    0 -> {
                                        val speedState = rememberLazyListScrollSpeedState(firstLazyListState)
                                        LazyColumn(
                                            state = firstLazyListState,
                                            contentPadding = contentPadding,
                                            userScrollEnabled = gradesViewModel.userScrollEnabled,
                                        ) {
                                            items(items.sortedWith(compareByDescending<GradeCollection> { it.givenAt }.thenBy { it.name }).toList()) {
                                                EnhancedAnimated(
                                                    modifier = Modifier.padding(verticalPadding),
                                                    preset = ZoomIn(),
                                                    durationMillis = 200,
                                                ) { isAnimated ->
                                                    LaunchedEffect(Unit) {
                                                        if (firstLazyListState.isScrollInProgress && isAnimated && abs(speedState.pxPerSecond) > 4000) {
                                                            vibrator.enhancedVibrate(EnhancedVibrations.LOW_TICK)
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
                                                                        Text(
                                                                            "${if (showTeachersWithFirstname) {
                                                                                it.conductor?.forename
                                                                            } else {
                                                                                it.conductor?.forename?.take(
                                                                                    1,
                                                                                ) + "."
                                                                            }} ${it.conductor?.name}: ${translateHistoryBody(it.body)}",
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        leadingContent = {
                                                            GradeValueBox(it.grades?.getOrNull(0)?.value)
                                                        },
                                                        colors = ListItemDefaults.colors(Color.Transparent),
                                                        modifier = Modifier.hazeSource(viewModel.hazeBackgroundState2),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    1 -> {
                                        val speedState = rememberLazyListScrollSpeedState(secondLazyListState)
                                        LazyColumn(
                                            state = secondLazyListState,
                                            modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
                                            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
                                            userScrollEnabled = gradesViewModel.userScrollEnabled,
                                        ) {
                                            items
                                                .sortedWith(compareBy({ it.subject?.name }, { it.givenAt }, { it.name }))
                                                .groupBy { it.subject?.name }
                                                .toList()
                                                .forEach { (title, subjectItems) ->
                                                    stickyHeader {
                                                        EnhancedAnimated(
                                                            preset = ZoomIn(),
                                                            durationMillis = 200,
                                                        ) { isAnimated ->
                                                            LaunchedEffect(Unit) {
                                                                if (secondLazyListState.isScrollInProgress && isAnimated && abs(speedState.pxPerSecond) > 4000) {
                                                                    vibrator.enhancedVibrate(EnhancedVibrations.LOW_TICK)
                                                                }
                                                            }
                                                            val subjectKey = remember(title, subjectItems) { subjectWeightingKey(title, subjectItems) }
                                                            val averageText =
                                                                remember(gradeAverageEnabled, subjectKey, subjectWeightings.toMap(), gradeAverageUseWeighting) {
                                                                    if (gradeAverageEnabled) {
                                                                        val typeNames = subjectTypeNames(subjectItems)
                                                                        val subjectWeighting =
                                                                            subjectWeightings[subjectKey] ?: loadSubjectWeighting(
                                                                                kSafe = kSafe,
                                                                                subjectKey = subjectKey,
                                                                                typeNames = typeNames,
                                                                                useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                                                                            )
                                                                        formatAverageLabel(
                                                                            calculateSubjectAverage(
                                                                                collections = subjectItems,
                                                                                weighting = subjectWeighting,
                                                                                useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                                                                            ),
                                                                        )
                                                                    } else {
                                                                        null
                                                                    }
                                                                }
                                                            val interactionSource = remember { MutableInteractionSource() }
                                                            Column(
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .height(56.dp)
                                                                    .hoverable(interactionSource)
                                                                    .indication(interactionSource, ripple())
                                                                    .pointerInput(Unit) {
                                                                        detectTapGestures(
                                                                            onPress = { offset ->
                                                                                val press = PressInteraction.Press(offset)
                                                                                interactionSource.emit(press)
                                                                                tryAwaitRelease()
                                                                                interactionSource.emit(PressInteraction.Release(press))
                                                                            },
                                                                            onTap = {
                                                                                vibrator.enhancedVibrate(EnhancedVibrations.CLICK)
                                                                                weightingDialogState =
                                                                                    SubjectWeightingDialogState(
                                                                                        subjectTitle = title ?: "Kein Fach",
                                                                                        subjectKey = subjectKey,
                                                                                        subjectCollections = subjectItems,
                                                                                    )
                                                                                weightingDialogVisible = true
                                                                            },
                                                                        )
                                                                    },
                                                            ) {
                                                                HorizontalDivider(thickness = 1.dp)
                                                                Box(Modifier.weight(1f)) {
                                                                    Box(
                                                                        Modifier
                                                                            .fillMaxSize()
                                                                            .enhancedHazeEffect(viewModel.hazeBackgroundState3, colorScheme.secondaryContainer)
                                                                            .enhancedHazeEffect(viewModel.hazeBackgroundState2, colorScheme.secondaryContainer) {
                                                                                mask =
                                                                                    Brush.verticalGradient(
                                                                                        colors = listOf(Color.Transparent, Color.Red),
                                                                                    )
                                                                            },
                                                                    )
                                                                    Row(
                                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                    ) {
                                                                        Text(
                                                                            text = title ?: "Kein Fach",
                                                                            modifier = Modifier.weight(1f),
                                                                            style = typography.titleMedium,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis,
                                                                        )
                                                                        if (averageText != null) {
                                                                            Text(
                                                                                text = averageText,
                                                                                style = typography.titleMedium,
                                                                                fontFamily = FontFamilies.Sniglet,
                                                                                maxLines = 1,
                                                                                overflow = TextOverflow.Ellipsis,
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                                HorizontalDivider(thickness = 1.dp)
                                                            }
                                                        }
                                                    }
                                                    items(subjectItems) {
                                                        EnhancedAnimated(
                                                            modifier = Modifier.padding(verticalPadding),
                                                            preset = ZoomIn(),
                                                            durationMillis = 200,
                                                        ) { isAnimated ->
                                                            LaunchedEffect(Unit) {
                                                                if (secondLazyListState.isScrollInProgress && isAnimated && abs(speedState.pxPerSecond) > 4000) {
                                                                    vibrator.enhancedVibrate(EnhancedVibrations.LOW_TICK)
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
                                                                                Text(
                                                                                    "${if (showTeachersWithFirstname) {
                                                                                        it.conductor?.forename
                                                                                    } else {
                                                                                        it.conductor?.forename?.take(
                                                                                            1,
                                                                                        ) + "."
                                                                                    }} ${it.conductor?.name}: ${translateHistoryBody(it.body)}",
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                },
                                                                leadingContent = {
                                                                    GradeValueBox(it.grades?.getOrNull(0)?.value)
                                                                },
                                                                colors = ListItemDefaults.colors(Color.Transparent),
                                                                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState2),
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
                topAppBarBackground(gradesViewModel.topPadding)
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier =
                        Modifier
                            .padding(verticalPadding)
                            .padding(top = innerPadding.calculateTopPadding())
                            .onGloballyPositioned {
                                gradesViewModel.topPadding = with(density) { it.size.height.toDp() } + innerPadding.calculateTopPadding()
                            },
                    containerColor = Color.Transparent,
                    divider = {
                        HorizontalDivider(thickness = if (pagerState.currentPage == 0) 2.dp else 1.dp)
                    },
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
                                text = "Nach Fächern",
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }

                SharedTransitionLayout(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -(toolbarContentPadding.calculateBottomPadding() + 12.dp))
                            .padding(verticalPadding)
                            .consumeWindowInsets(toolbarContentPadding)
                            .imePadding(),
                ) {
                    val sharedContentState = rememberSharedContentState(key = "toolbar-card")
                    val ime = WindowInsets.ime

                    var backProgress by remember { mutableFloatStateOf(0f) }
                    var isBackInProgress by remember { mutableStateOf(false) }
                    @Suppress("DEPRECATION")
                    PredictiveBackHandler(enabled = gradesViewModel.toolbarState != 0 && !gradesViewModel.isLoading) { progressFlow ->
                        try {
                            isBackInProgress = true

                            progressFlow.collect { event ->
                                backProgress = event.progress
                            }

                            scope.launch {
                                gradesViewModel.searchQuery = ""
                                gradesViewModel.toolbarState = 0
                                gradesViewModel.contentBlurred = false
                                delay(250)
                                if (gradesViewModel.toolbarState == 0) gradesViewModel.userScrollEnabled = true
                                isBackInProgress = false
                                backProgress = 0f
                            }
                        } catch (_: CancellationException) {
                            isBackInProgress = false
                            backProgress = 0f
                        }
                    }
                    val backHandlingModifier =
                        if (isBackInProgress) {
                            Modifier.scale(1f - (backProgress * 0.2f))
                        } else {
                            Modifier
                        }

                    AnimatedContent(
                        targetState = gradesViewModel.toolbarState,
                        modifier =
                            Modifier.padding(top = gradesViewModel.topPadding + 24.dp + innerPadding.calculateBottomPadding()).onGloballyPositioned {
                                gradesViewModel.toolbarPadding = with(density) { ime.getBottom(density).toDp() + it.size.height.toDp() + 12.dp }
                            },
                        contentAlignment = Alignment.BottomCenter,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(250)) togetherWith
                                fadeOut(animationSpec = tween(250)) using
                                SizeTransform(
                                    clip = false,
                                    sizeAnimationSpec = { _, _ ->
                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                    },
                                )
                        },
                    ) { targetState ->
                        when (targetState) {
                            0 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    HorizontalFloatingToolbar(
                                        expanded = if (animationsEnabled) currentLazyListState.isScrollingUp() else true,
                                        modifier =
                                            Modifier
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                    renderInOverlayDuringTransition = false,
                                                ).clip(FloatingToolbarDefaults.ContainerShape)
                                                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(Color.Transparent),
                                        leadingContent = {
                                            EnhancedIconButton(
                                                onClick = {
                                                    gradesViewModel.toolbarState = 1
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                },
                                                enabled = !gradesViewModel.isLoading,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Search,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                            EnhancedIconButton(
                                                onClick = {
                                                    gradesViewModel.toolbarState = 2
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                    gradesViewModel.userScrollEnabled = false
                                                    gradesViewModel.contentBlurred = true
                                                },
                                                enabled = viewModel.years.isNotEmpty() && !gradesViewModel.isLoading,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.CalendarMonth,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                        },
                                        trailingContent = {
                                            EnhancedIconButton(
                                                onClick = {
                                                    gradesViewModel.refreshGrades(viewModel)
                                                },
                                                enabled = viewModel.years.isNotEmpty() && !gradesViewModel.isLoading,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Refresh,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                            EnhancedIconButton(
                                                onClick = {
                                                    gradesViewModel.toolbarState = 3
                                                    isBackInProgress = false
                                                    backProgress = 0f
                                                    gradesViewModel.userScrollEnabled = false
                                                    gradesViewModel.contentBlurred = true
                                                },
                                                enabled = !gradesViewModel.isLoading,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Settings,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                        },
                                        collapsedShadowElevation = 0.dp,
                                        expandedShadowElevation = 0.dp,
                                    ) {
                                        EnhancedIconButton(
                                            onClick = {
                                                gradesViewModel.toolbarState = 4
                                                isBackInProgress = false
                                                backProgress = 0f
                                                gradesViewModel.userScrollEnabled = false
                                                gradesViewModel.contentBlurred = true
                                            },
                                            enabled = !gradesViewModel.isLoading,
                                            modifier = Modifier.width(64.dp),
                                            colors =
                                                IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = colorScheme.primary.copy(0.5f),
                                                    disabledContainerColor = Color.Transparent,
                                                ),
                                        ) { enabled ->
                                            Icon(
                                                imageVector = Icons.Outlined.BarChart,
                                                contentDescription = null,
                                                tint = if (enabled) colorScheme.onPrimary else colorScheme.onPrimaryContainer,
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    Card(
                                        modifier =
                                            Modifier
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                ).then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            val focusRequester = remember { FocusRequester() }
                                            LaunchedEffect(Unit) {
                                                focusRequester.requestFocus()
                                            }
                                            EnhancedIconButton(
                                                onClick = {},
                                                enabled = false,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Search,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                            BasicTextField(
                                                value = gradesViewModel.searchQuery,
                                                onValueChange = { gradesViewModel.searchQuery = it },
                                                modifier = Modifier.weight(1f).padding(vertical = 15.dp).focusRequester(focusRequester),
                                                singleLine = true,
                                                textStyle = TextStyle.Default.copy(colorScheme.onPrimaryContainer, 20.sp),
                                                cursorBrush = SolidColor(colorScheme.onPrimaryContainer),
                                            )
                                            EnhancedIconButton(
                                                onClick = {
                                                    gradesViewModel.searchQuery = ""
                                                    gradesViewModel.closeToolbar()
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Close,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            2 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    Card(
                                        modifier =
                                            Modifier
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                ).then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent),
                                    ) {
                                        LaunchedEffect(Unit) {
                                            if (!viewModel.allGradeCollectionsLoaded.value) {
                                                gradesViewModel.isLoading = true
                                                viewModel.gradeCollections.clear()
                                                viewModel.getCollections(viewModel.years)?.let {
                                                    viewModel.gradeCollections.addAll(it)
                                                    viewModel.allGradeCollectionsLoaded.value = true
                                                    gradesViewModel.isLoading = false
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
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                items(viewModel.years) { year ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        EnhancedCheckbox(
                                                            checked = gradesViewModel.selectedYears.contains(year),
                                                            onCheckedChange = {
                                                                if (gradesViewModel.selectedYears.contains(year)) {
                                                                    gradesViewModel.selectedYears.remove(year)
                                                                } else {
                                                                    gradesViewModel.selectedYears.add(year)
                                                                }
                                                            },
                                                            enabled = !gradesViewModel.isLoading,
                                                        )
                                                        Text(
                                                            text = "${year.name} (${formateDate(year.from)} - ${formateDate(year.to)})",
                                                            style = typography.bodyLarge,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        EnhancedButton(
                                            onClick = {
                                                gradesViewModel.closeToolbar()
                                            },
                                            enabled = !gradesViewModel.isLoading,
                                            modifier = Modifier.padding(10.dp).align(Alignment.End),
                                        ) {
                                            EnhancedAnimatedContent(gradesViewModel.isLoading) {
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
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    Card(
                                        modifier =
                                            Modifier
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                ).then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 500.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                                        colors = CardDefaults.cardColors(Color.Transparent),
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
                                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                            ) {
                                                settingsToggleItem(
                                                    checked = gradeAverageEnabled,
                                                    onCheckedChange = {
                                                        gradeAverageEnabled = it
                                                        kSafe.putDirect("gradeAverageEnabled", it)
                                                        if (!it) {
                                                            weightingDialogVisible = false
                                                        }
                                                    },
                                                    text = "Durchschnittsberechnung aktiv",
                                                    icon = MathAvg,
                                                    textModifier = Modifier.skipToLookaheadSize(),
                                                    position = PreferencePosition.Top,
                                                )
                                                settingsToggleItem(
                                                    checked = gradeAverageUseWeighting,
                                                    onCheckedChange = {
                                                        if (gradeAverageUseWeighting != it) {
                                                            gradeAverageUseWeighting = it
                                                            kSafe.putDirect("gradeAverageUseWeighting", it)
                                                            convertStoredSubjectWeightingsMode(
                                                                kSafe = kSafe,
                                                                useWeightingInsteadOfPercent = it,
                                                            )
                                                            subjectWeightings.clear()
                                                        }
                                                    },
                                                    text = "Mit Gewichtungen statt Prozenten rechnen",
                                                    icon = Icons.Outlined.Balance,
                                                    textModifier = Modifier.skipToLookaheadSize(),
                                                    position = PreferencePosition.Middle,
                                                )
                                                settingsToggleItem(
                                                    checked = showGradeHistory,
                                                    onCheckedChange = {
                                                        showGradeHistory = it
                                                        kSafe.putDirect("showGradeHistory", it)
                                                    },
                                                    text = "Noten-Historien anzeigen",
                                                    icon = Icons.Outlined.History,
                                                    textModifier = Modifier.skipToLookaheadSize(),
                                                    position = if (viewModel.isDemoAccount.value) PreferencePosition.Bottom else PreferencePosition.Middle,
                                                )
                                                if (!viewModel.isDemoAccount.value) {
                                                    settingsToggleItem(
                                                        checked = showCollectionsWithoutGrades,
                                                        onCheckedChange = {
                                                            showCollectionsWithoutGrades = it
                                                            kSafe.putDirect("showCollectionsWithoutGrades", it)
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
                                                            kSafe.putDirect("showTeachersWithFirstname", it)
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
                                                gradesViewModel.closeToolbar()
                                            },
                                            modifier = Modifier.padding(10.dp).align(Alignment.End),
                                        ) {
                                            Text("Schließen")
                                        }
                                    }
                                }
                            }
                            4 -> {
                                Box(
                                    contentAlignment = Alignment.BottomCenter,
                                ) {
                                    Card(
                                        modifier =
                                            Modifier
                                                .enhancedSharedBounds(
                                                    sharedTransitionScope = sharedTransitionScope,
                                                    sharedContentState = sharedContentState,
                                                    animatedVisibilityScope = this@AnimatedContent,
                                                    boundsTransform = { _, _ ->
                                                        spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                    },
                                                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                                ).then(backHandlingModifier)
                                                .padding(horizontal = 12.dp)
                                                .sizeIn(maxWidth = 600.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.surfaceContainerHighest),
                                        colors = CardDefaults.cardColors(Color.Transparent),
                                    ) {
                                        GradeDiagrams(viewModel, gradesViewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        weightingDialogState?.let { dialogState ->
            val dialogTypeNames = subjectTypeNames(dialogState.subjectCollections)
            val currentWeighting =
                subjectWeightings[dialogState.subjectKey] ?: loadSubjectWeighting(
                    kSafe = kSafe,
                    subjectKey = dialogState.subjectKey,
                    typeNames = dialogTypeNames,
                    useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                )

            GradeWeightingDialog(
                visible = weightingDialogVisible && gradeAverageEnabled,
                subjectTitle = dialogState.subjectTitle,
                collections = dialogState.subjectCollections,
                weighting = currentWeighting,
                useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                onDismissRequest = {
                    scope.launch {
                        weightingDialogVisible = false
                        delay(300)
                        weightingDialogState = null
                    }
                },
                onCategoryWeightChanged = { categoryId, weight ->
                    val latestWeighting =
                        subjectWeightings[dialogState.subjectKey] ?: loadSubjectWeighting(
                            kSafe = kSafe,
                            subjectKey = dialogState.subjectKey,
                            typeNames = dialogTypeNames,
                            useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                        )
                    val updatedWeighting = latestWeighting.withCategoryWeight(categoryId, weight)
                    subjectWeightings[dialogState.subjectKey] = updatedWeighting
                    persistSubjectWeighting(
                        kSafe = kSafe,
                        subjectKey = dialogState.subjectKey,
                        weighting = updatedWeighting,
                    )
                },
                onTypeCategoryChanged = { typeName, categoryId ->
                    val latestWeighting =
                        subjectWeightings[dialogState.subjectKey] ?: loadSubjectWeighting(
                            kSafe = kSafe,
                            subjectKey = dialogState.subjectKey,
                            typeNames = dialogTypeNames,
                            useWeightingInsteadOfPercent = gradeAverageUseWeighting,
                        )
                    val updatedWeighting = latestWeighting.withTypeCategory(typeName, categoryId)
                    subjectWeightings[dialogState.subjectKey] = updatedWeighting
                    persistSubjectWeighting(
                        kSafe = kSafe,
                        subjectKey = dialogState.subjectKey,
                        weighting = updatedWeighting,
                    )
                },
                onResetToDefault = {
                    subjectWeightings[dialogState.subjectKey] = defaultSubjectWeightingConfig(gradeAverageUseWeighting)
                    clearSubjectWeighting(
                        kSafe = kSafe,
                        subjectKey = dialogState.subjectKey,
                        typeNames = dialogTypeNames,
                    )
                },
            )
        }
    }
}
