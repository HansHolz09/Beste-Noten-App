package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedPagerState
import com.hansholz.bestenotenapp.main.LocalShowAbsences
import com.hansholz.bestenotenapp.main.ViewModel
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalTime::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun Timetable(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val timetableViewModel = viewModel { TimetableViewModel(viewModel) }

        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val hapticFeedback = LocalHapticFeedback.current

        var showAbsences by LocalShowAbsences.current

        @Suppress("DEPRECATION")
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        TopAppBarScaffold(
            modifier =
                Modifier.enhancedSharedBounds(
                    sharedTransitionScope = sharedTransitionScope,
                    sharedContentState = rememberSharedContentState(key = "timetable-card"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            title = "Stundenplan",
            titleModifier =
                Modifier
                    .enhancedSharedElement(
                        sharedTransitionScope = sharedTransitionScope,
                        sharedContentState = rememberSharedContentState(key = "timetable-title"),
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
                var topPadding by remember { mutableStateOf(innerPadding.calculateTopPadding()) }
                val toolbarContentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())
                val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding() + timetableViewModel.toolbarPadding)
                val verticalPadding = PaddingValues(start = innerPadding.calculateStartPadding(layoutDirection), end = innerPadding.calculateEndPadding(layoutDirection))

                val lessonPopupShown = remember { mutableStateOf(false) }
                val pagerState = rememberEnhancedPagerState(Int.MAX_VALUE, Int.MAX_VALUE / 2)
                val contentBlurRadius = animateDpAsState(if (timetableViewModel.contentBlurred) 10.dp else 0.dp)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.hazeSource(viewModel.hazeBackgroundState).enhancedHazeEffect(blurRadius = contentBlurRadius.value),
                    beyondViewportPageCount = 1,
                    userScrollEnabled = timetableViewModel.userScrollEnabled && !lessonPopupShown.value,
                ) { currentPage ->
                    var isLoading by remember { mutableStateOf(false) }
                    var weekDate = remember { timetableViewModel.startPageDate.plus(currentPage - (Int.MAX_VALUE / 2), DateTimeUnit.WEEK) }
                    var week by remember { mutableStateOf<JournalWeek?>(null) }
                    LaunchedEffect(weekDate, timetableViewModel.startPageDate) {
                        isLoading = true
                        while (viewModel.years.isEmpty() || (viewModel.absences.isEmpty() && showAbsences)) delay(10)
                        weekDate = timetableViewModel.startPageDate.plus(currentPage - (Int.MAX_VALUE / 2), DateTimeUnit.WEEK)
                        week = viewModel.getJournalWeek(weekDate, getAbsences = showAbsences && pagerState.currentPage == currentPage)
                        isLoading = false
                    }
                    var isRefreshLoading by remember { mutableStateOf(false) }
                    val pullToRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = isRefreshLoading,
                        onRefresh = {
                            if (timetableViewModel.userScrollEnabled) {
                                scope.launch {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                    isRefreshLoading = true
                                    week = viewModel.getJournalWeek(weekDate, false, showAbsences)
                                    isRefreshLoading = false
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                }
                            }
                        },
                        state = pullToRefreshState,
                        indicator = {
                            if (timetableViewModel.userScrollEnabled) {
                                PullToRefreshDefaults.LoadingIndicator(
                                    modifier = Modifier.align(Alignment.TopCenter).padding(topPadding),
                                    isRefreshing = isRefreshLoading,
                                    state = pullToRefreshState,
                                )
                            }
                        },
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AnimatedContent(isLoading || week?.days?.all { it.lessons.isNullOrEmpty() } ?: true) { targetState ->
                                        Box(Modifier.fillMaxSize()) {
                                            if (targetState) {
                                                AnimatedContent(isLoading) { isLoading ->
                                                    if (isLoading) {
                                                        Box(
                                                            modifier = Modifier.padding(contentPadding).fillMaxSize(),
                                                            contentAlignment = Alignment.Center,
                                                        ) {
                                                            ContainedLoadingIndicator()
                                                        }
                                                    } else {
                                                        EmptyStateMessage(
                                                            title = "Keine Stunden für diese Woche gefunden",
                                                            icon = Icons.Outlined.EventBusy,
                                                            modifier = Modifier.padding(contentPadding).consumeWindowInsets(contentPadding).imePadding(),
                                                        )
                                                    }
                                                }
                                            } else {
                                                WeekScheduleView(
                                                    week = week,
                                                    absences = if (showAbsences) viewModel.absences.flatMap { it.second } else emptyList(),
                                                    lessonPopupShown = lessonPopupShown,
                                                    isCurrentPage = currentPage == pagerState.currentPage,
                                                    contentPadding = contentPadding,
                                                    modifier = Modifier.padding(bottom = 10.dp).padding(horizontal = 6.dp),
                                                    enabled = timetableViewModel.userScrollEnabled,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                topAppBarBackground(innerPadding.calculateTopPadding())

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
                    PredictiveBackHandler(enabled = timetableViewModel.toolbarState != 0) { progressFlow ->
                        try {
                            isBackInProgress = true

                            progressFlow.collect { event ->
                                backProgress = event.progress
                            }

                            scope.launch {
                                timetableViewModel.toolbarState = 0
                                timetableViewModel.contentBlurred = false
                                delay(250)
                                if (timetableViewModel.toolbarState == 0) timetableViewModel.userScrollEnabled = true
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
                        targetState = timetableViewModel.toolbarState,
                        modifier = Modifier.padding(top = topPadding + 24.dp + innerPadding.calculateBottomPadding()),
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
                                        expanded = true,
                                        modifier =
                                            Modifier
                                                .onGloballyPositioned {
                                                    timetableViewModel.toolbarPadding = with(density) { ime.getBottom(density).toDp() + it.size.height.toDp() + 12.dp }
                                                }.enhancedSharedBounds(
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
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                                    }
                                                },
                                                enabled = !lessonPopupShown.value,
                                                hapticEnabled = false,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer,
                                                )
                                            }
                                        },
                                        trailingContent = {
                                            EnhancedIconButton(
                                                onClick = {
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                                    }
                                                },
                                                enabled = !lessonPopupShown.value,
                                                hapticEnabled = false,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
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
                                                timetableViewModel.toolbarState = 1
                                                isBackInProgress = false
                                                backProgress = 0f
                                                timetableViewModel.userScrollEnabled = false
                                                timetableViewModel.contentBlurred = true
                                            },
                                            enabled = !lessonPopupShown.value,
                                            modifier = Modifier.width(64.dp),
                                            colors =
                                                IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = colorScheme.primary.copy(0.5f),
                                                    disabledContainerColor = Color.Transparent,
                                                ),
                                        ) { enabled ->
                                            Icon(
                                                imageVector = Icons.Outlined.CalendarMonth,
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
                                                .verticalScroll(rememberScrollState()),
                                        colors = CardDefaults.cardColors(colorScheme.primaryContainer),
                                    ) {
                                        Text(
                                            text = "Datum wählen",
                                            modifier = Modifier.padding(15.dp).align(Alignment.CenterHorizontally),
                                            color = colorScheme.onPrimaryContainer,
                                            style = typography.headlineSmall,
                                        )
                                        val datePickerState =
                                            rememberDatePickerState(
                                                initialSelectedDateMillis =
                                                    timetableViewModel.startPageDate
                                                        .plus(pagerState.currentPage - (Int.MAX_VALUE / 2), DateTimeUnit.WEEK)
                                                        .plus(1, DateTimeUnit.DAY)
                                                        .atStartOfDayIn(TimeZone.currentSystemDefault())
                                                        .toEpochMilliseconds(),
                                            )
                                        DatePicker(
                                            state = datePickerState,
                                            modifier = Modifier.requiredHeight(420.dp).requiredWidth(400.dp).skipToLookaheadSize(),
                                            colors =
                                                DatePickerDefaults.colors(
                                                    containerColor = colorScheme.primaryContainer,
                                                    headlineContentColor = colorScheme.onSurface,
                                                    weekdayContentColor = colorScheme.onPrimaryContainer,
                                                    navigationContentColor = colorScheme.onSurface,
                                                    yearContentColor = colorScheme.onSurface,
                                                    dividerColor = colorScheme.onSurface,
                                                ),
                                            title = null,
                                            headline = {
                                                AnimatedContent(datePickerState.selectedDateMillis) { selectedDateMillis ->
                                                    ProvideTextStyle(LocalTextStyle.current.copy(fontSize = 22.sp)) {
                                                        DatePickerDefaults.DatePickerHeadline(
                                                            selectedDateMillis = selectedDateMillis,
                                                            displayMode = datePickerState.displayMode,
                                                            dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                                                            modifier = Modifier.padding(PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)),
                                                            contentColor = colorScheme.onSurface,
                                                        )
                                                    }
                                                }
                                            },
                                        )
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    timetableViewModel.toolbarState = 0
                                                    timetableViewModel.contentBlurred = false
                                                    val selectedDate =
                                                        Instant
                                                            .fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                                            .date
                                                    timetableViewModel.startPageDate = selectedDate
                                                    pagerState.scrollToPage(Int.MAX_VALUE / 2)
                                                    delay(250)
                                                    if (timetableViewModel.toolbarState == 0) timetableViewModel.userScrollEnabled = true
                                                }
                                            },
                                            enabled = datePickerState.selectedDateMillis != null,
                                            modifier = Modifier.padding(10.dp).align(Alignment.End),
                                        ) {
                                            Text("Wählen")
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
