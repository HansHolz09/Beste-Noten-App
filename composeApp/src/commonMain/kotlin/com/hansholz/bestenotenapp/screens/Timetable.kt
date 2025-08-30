package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.components.EmptyStateMessage
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.rememberEnhancedPagerState
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.utils.SimpleTime
import dev.chrisbanes.haze.hazeSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalTime::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun Timetable(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val hapticFeedback = LocalHapticFeedback.current
        @Suppress("DEPRECATION")
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val lessonPopupShown = remember { mutableStateOf(false) }

        TopAppBarScaffold(
            modifier = Modifier.enhancedSharedBounds(
                sharedTransitionScope = sharedTransitionScope,
                sharedContentState = rememberSharedContentState(key = "timetable-card"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            title = "Stundenplan",
            titleModifier = Modifier.enhancedSharedElement(
                sharedTransitionScope = sharedTransitionScope,
                sharedContentState = rememberSharedContentState(key = "timetable-title"),
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
                var topPadding by remember { mutableStateOf(innerPadding.calculateTopPadding()) }
                var toolbarPadding by remember { mutableStateOf(0.dp) }
                val toolbarContentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())
                val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding() + toolbarPadding)
                val verticalPadding = PaddingValues(start = innerPadding.calculateStartPadding(layoutDirection), end = innerPadding.calculateEndPadding(layoutDirection))

                var startPageDate by rememberSaveable { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
                val pagerState = rememberEnhancedPagerState(Int.MAX_VALUE, Int.MAX_VALUE / 2)
                var userScrollEnabled by remember { mutableStateOf(true) }
                var contentBlurred by remember { mutableStateOf(false) }
                val contentBlurRadius = animateDpAsState(if (contentBlurred) 10.dp else 0.dp)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.hazeSource(viewModel.hazeBackgroundState).enhancedHazeEffect(blurRadius = contentBlurRadius.value),
                    userScrollEnabled = userScrollEnabled && !lessonPopupShown.value
                ) { currentPage ->
                    var isLoading by remember { mutableStateOf(false) }
                    val weekDate = remember { startPageDate.plus(currentPage - (Int.MAX_VALUE / 2), DateTimeUnit.WEEK) }
                    var week by remember { mutableStateOf<JournalWeek?>(null) }
                    LaunchedEffect(Unit) {
                        isLoading = true
                        week = viewModel.getJournalWeek(weekDate)
                        isLoading = false
                        if (viewModel.years.isEmpty()) {
                            viewModel.getYears()?.let { viewModel.years.addAll(it) }
                        }
                    }
                    var isRefreshLoading by remember { mutableStateOf(false) }
                    val pullToRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = isRefreshLoading,
                        onRefresh = {
                            scope.launch {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                isRefreshLoading = true
                                week = viewModel.getJournalWeek(weekDate, false)
                                isRefreshLoading = false
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            }
                        },
                        state = pullToRefreshState,
                        indicator = {
                            PullToRefreshDefaults.LoadingIndicator(
                                modifier = Modifier.align(Alignment.TopCenter).padding(topPadding),
                                isRefreshing = isRefreshLoading,
                                state = pullToRefreshState,
                            )
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AnimatedContent(isLoading || week?.days?.all { it.lessons.isNullOrEmpty() } ?: true) { targetState ->
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
                                                            title = "Keine Stunden für diese Woche gefunden",
                                                            icon = Icons.Outlined.EventBusy,
                                                            modifier = Modifier.padding(contentPadding).consumeWindowInsets(contentPadding).imePadding()
                                                        )
                                                    }
                                                }
                                            } else {
                                                WeekScheduleView(
                                                    week = week,
                                                    lessonPopupShown = lessonPopupShown,
                                                    isCurrentPage = currentPage == pagerState.currentPage,
                                                    contentPadding = contentPadding,
                                                    modifier = Modifier.padding(bottom = 10.dp).padding(horizontal = 6.dp)
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
                    PredictiveBackHandler(enabled = state != 0) { progressFlow ->
                        try {
                            isBackInProgress = true

                            progressFlow.collect { event ->
                                backProgress = event.progress
                            }

                            scope.launch {
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
                        modifier = Modifier.padding(top = topPadding + 24.dp + innerPadding.calculateBottomPadding()),
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
                                        expanded = true,
                                        modifier = Modifier
                                            .onGloballyPositioned {
                                                toolbarPadding = with(density) { ime.getBottom(density).toDp() + it.size.height.toDp() + 12.dp }
                                            }
                                            .enhancedSharedBounds(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = sharedContentState,
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ ->
                                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                                },
                                                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                            )
                                            .clip(FloatingToolbarDefaults.ContainerShape)
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
                                                hapticEnabled = false
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                                    contentDescription = null,
                                                    tint = colorScheme.onPrimaryContainer
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
                                                hapticEnabled = false
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
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
                                                state = 1
                                                isBackInProgress = false
                                                backProgress = 0f
                                                userScrollEnabled = false
                                                contentBlurred = true
                                            },
                                            enabled = !lessonPopupShown.value,
                                            modifier = Modifier.width(64.dp),
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = colorScheme.primary.copy(0.5f),
                                                disabledContainerColor = Color.Transparent
                                            )
                                        ) { enabled ->
                                            Icon(
                                                imageVector = Icons.Outlined.CalendarMonth,
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
                                            .clip(RoundedCornerShape(16.dp))
                                            .verticalScroll(rememberScrollState()),
                                        colors = CardDefaults.cardColors(colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            text = "Datum wählen",
                                            modifier = Modifier.padding(15.dp).align(Alignment.CenterHorizontally),
                                            color = colorScheme.onPrimaryContainer,
                                            style = typography.headlineSmall,
                                        )
                                        val datePickerState = rememberDatePickerState(
                                            initialSelectedDateMillis = startPageDate
                                                .plus(pagerState.currentPage - (Int.MAX_VALUE / 2), DateTimeUnit.WEEK)
                                                .plus(1, DateTimeUnit.DAY)
                                                .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                                        )
                                        DatePicker(
                                            state = datePickerState,
                                            colors = DatePickerDefaults.colors(
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
                                            }
                                        )
                                        EnhancedButton(
                                            onClick = {
                                                scope.launch {
                                                    state = 0
                                                    contentBlurred = false
                                                    val selectedDate = Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                                    startPageDate = selectedDate
                                                    pagerState.scrollToPage((Int.MAX_VALUE / 2) - 1)
                                                    pagerState.scrollToPage(Int.MAX_VALUE / 2)
                                                    delay(250)
                                                    if (state == 0) userScrollEnabled = true
                                                }
                                            },
                                            enabled = datePickerState.selectedDateMillis != null,
                                            modifier = Modifier.padding(10.dp).align(Alignment.End)
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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun WeekScheduleView(
    week: JournalWeek?,
    lessonPopupShown: MutableState<Boolean>,
    isCurrentPage: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val isDark = LocalThemeIsDark.current
    val blurEnabled = LocalBlurEnabled.current
    val hapticFeedback = LocalHapticFeedback.current
    val showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

    if (week?.days == null) return

    val allLessons = week.days
        .map {
            it.copy(lessons = it.lessons?.mapIndexed { index, lesson ->
                val startTime = SimpleTime.parse("07:30").plus(50 * index)
                if (lesson.time?.from == null || lesson.time.to == null) {
                    lesson.copy(time = lesson.time?.copy(from = startTime.toString(), to = startTime.plus(45).toString()))
                } else lesson
            })
        }
        .flatMap { it.lessons.orEmpty() }

    if (allLessons.isEmpty()) return

    val minTimeHour = allLessons.minOfOrNull { SimpleTime.parse(it.time?.from ?: "23:59") }
    val latestLessonEnd = allLessons.maxOfOrNull { SimpleTime.parse(it.time?.to ?: "00:00") }

    var selectedLesson by remember { mutableStateOf<JournalLesson?>(null) }
    var selectedDay by remember { mutableStateOf("") }

    var contentBlurred by remember { mutableStateOf(false) }
    val contentBlurRadius = animateDpAsState(if (contentBlurred) 10.dp else 0.dp)

    SharedTransitionLayout(
        modifier = Modifier
    ) {
        AnimatedContent(
            targetState = lessonPopupShown.value,
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
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
        ) { targetLessonPopupShown ->
            Row(modifier = Modifier.fillMaxHeight().enhancedHazeEffect(blurRadius = contentBlurRadius.value).padding(contentPadding).then(modifier)) {
                week.days.forEachIndexed { dayIndex, day ->
                    if (!day.lessons.isNullOrEmpty()) {
                        val currentDate = LocalDate.parse(week.days.firstOrNull()?.date ?: "01.01.2000").plus(dayIndex, DateTimeUnit.DAY)
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DayHeader(currentDate)
                            DailyScheduleLayout(
                                lessons = day.lessons,
                                modifier = Modifier.fillMaxHeight(),
                                minTime = minTimeHour ?: SimpleTime.parse("7:00"),
                                maxTime = latestLessonEnd ?: SimpleTime.parse("18:00"),
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this@AnimatedContent,
                                selectedLesson = selectedLesson,
                                shownLessonPopup = if (targetLessonPopupShown) selectedLesson else null,
                            ) { lesson ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                selectedLesson = lesson
                                selectedDay = "${
                                    when(currentDate.dayOfWeek) {
                                        DayOfWeek.MONDAY -> "Montag"
                                        DayOfWeek.TUESDAY -> "Dienstag"
                                        DayOfWeek.WEDNESDAY -> "Mittwoch"
                                        DayOfWeek.THURSDAY -> "Donnerstag"
                                        DayOfWeek.FRIDAY -> "Freitag"
                                        DayOfWeek.SATURDAY -> "Samstag"
                                        DayOfWeek.SUNDAY -> "Sonntag"
                                    }
                                }, ${currentDate.day.toString().padStart(2, '0')}." +
                                        "${currentDate.month.number.toString().padStart(2, '0')}.${currentDate.year}"
                                lessonPopupShown.value = true
                                contentBlurred = true
                            }
                        }
                    }
                }
            }
            if (targetLessonPopupShown) {
                var backProgress by remember { mutableFloatStateOf(0f) }
                var isBackInProgress by remember { mutableStateOf(false) }
                PredictiveBackHandler(isCurrentPage) { progressFlow ->
                    try {
                        isBackInProgress = true

                        progressFlow.collect { event ->
                            backProgress = event.progress
                        }

                        scope.launch {
                            lessonPopupShown.value = false
                            contentBlurred = false
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
                Box(Modifier.fillMaxSize().clickable(null, null) {
                    lessonPopupShown.value = false
                    contentBlurred = false
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                }) {
                    OutlinedCard(
                        modifier = backHandlingModifier
                            .verticalScroll(rememberScrollState())
                            .widthIn(max = 350.dp)
                            .align(Alignment.Center)
                            .padding(contentPadding)
                            .padding(10.dp)
                            .enhancedSharedBounds(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                sharedContentState = rememberSharedContentState(selectedLesson ?: ""),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = { _, _ ->
                                    spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                },
                            )
                            .clickable(null, null) {},
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = when(selectedLesson?.status) {
                                "hold" -> if (isDark) Color(48, 99, 57) else Color(226, 251, 232)
                                "canceled" -> colorScheme.errorContainer
                                "initial" -> if (isDark) Color.DarkGray else Color.LightGray
                                "planned" -> if (isDark) Color(38, 63, 168) else Color(222, 233, 252)
                                else -> colorScheme.surface
                            }.copy(if (blurEnabled.value) 0.7f else 1f)
                        ),
                        border = BorderStroke(2.dp, colorScheme.outline)
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            EnhancedIconButton(
                                onClick = {
                                    lessonPopupShown.value = false
                                    contentBlurred = false
                                },
                            ) {
                                Icon(Icons.Outlined.Close, null)
                            }
                            Text(
                                text = selectedLesson?.subject?.localId ?: "?",
                                modifier = Modifier.align(Alignment.Center).skipToLookaheadSize(),
                                style = typography.headlineLarge
                            )
                        }
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = selectedLesson?.subject?.name ?: "Unbekanntes Fach",
                                    style = typography.headlineSmall
                                )
                            },
                            overlineContent = {
                                Text(
                                    text = "${selectedDay}\n${selectedLesson?.nr ?: "?"}. Stunde" +
                                            if (week.days.flatMap { it.lessons.orEmpty() }.find { it == selectedLesson }?.time?.from != null) {
                                                " (${selectedLesson?.time?.from} - ${selectedLesson?.time?.to})"
                                            } else "",
                                )
                            },
                            supportingContent = {
                                Text(
                                    when(selectedLesson?.status) {
                                        "hold" -> "gehalten"
                                        "canceled" -> "abgesagt"
                                        "initial" -> "initial"
                                        "planned" -> "geplant"
                                        else -> "unbekannter Status"
                                    }
                                )
                            },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f))
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = { Text(selectedLesson?.group?.name?.ifEmpty { "Unbekannt" } ?: "Unbekannt") },
                            overlineContent = { Text("Klasse/Gruppe") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f))
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = { Text(selectedLesson?.teachers?.joinToString { ((if (showTeachersWithFirstname) it.forename else it.forename?.take(1) + ".") + " " + it.name) }?.ifEmpty { "Unbekannt" } ?: "Unbekannt" ) },
                            overlineContent = { Text("Lehrer/-in(nen)") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f))
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = { Text(selectedLesson?.rooms?.joinToString { it.localId }?.ifEmpty { "Unbekannt" } ?: "Unbekannt") },
                            overlineContent = { Text("Raum/Räume") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f))
                        )
                        selectedLesson?.notes?.forEach { note ->
                            HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                            ListItem(
                                headlineContent = { Text(note.description ?: "Leer") },
                                overlineContent = { Text(note.type?.name?.replace("Substitution Plan", "Vertretungsplan") ?: "Unbenannte Notiz") },
                                trailingContent = { note.type?.color?.let { Box(Modifier.size(15.dp).clip(CircleShape).background(Color(it.removePrefix("#").toLong(16) or 0x00000000FF000000))) } },
                                modifier = Modifier.skipToLookaheadSize(),
                                colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun DayHeader(date: LocalDate) {
    fun getDayAbbreviation(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mo"
            DayOfWeek.TUESDAY -> "Di"
            DayOfWeek.WEDNESDAY -> "Mi"
            DayOfWeek.THURSDAY -> "Do"
            DayOfWeek.FRIDAY -> "Fr"
            DayOfWeek.SATURDAY -> "Sa"
            DayOfWeek.SUNDAY -> "So"
        }
    }

    val isCurrentDate = (date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val color = if (isCurrentDate) colorScheme.primary else null

    val dayAbbreviation = getDayAbbreviation(date.dayOfWeek)
    val formattedDate = "${date.day.toString().padStart(2, '0')}.${date.month.number.toString().padStart(2, '0')}."

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = dayAbbreviation, color = color ?: Color.Unspecified, fontWeight = FontWeight.Bold)
        Text(text = formattedDate, color = color ?: Color.Gray)
    }
}

private data class LessonPlacementInfo(val x: Int, val y: Int, val width: Int, val height: Int)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DailyScheduleLayout(
    lessons: List<JournalLesson>,
    modifier: Modifier = Modifier,
    minTime: SimpleTime,
    maxTime: SimpleTime,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    selectedLesson: JournalLesson?,
    shownLessonPopup: JournalLesson?,
    onLessonPopupOpened: (JournalLesson) -> Unit,
) {
    val sortedLessons = lessons
        .sortedBy { it.nr }
        .mapIndexed { index, lesson ->
            val startTime = SimpleTime.parse("07:30").plus(50 * index)
            if (lesson.time?.from == null || lesson.time.to == null) {
                lesson.copy(time = lesson.time?.copy(from = startTime.toString(), to = startTime.plus(45).toString()))
            } else lesson
        }

    with(sharedTransitionScope) {
        Layout(
            modifier = modifier,
            content = {
                val isDark = LocalThemeIsDark.current
                sortedLessons.forEach { lesson ->
                    Box {
                        if (shownLessonPopup != lesson) {
                            OutlinedCard(
                                onClick = {
                                    onLessonPopupOpened(lesson)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp)
                                    .enhancedSharedBounds(
                                        sharedTransitionScope = sharedTransitionScope,
                                        sharedContentState = rememberSharedContentState(lesson),
                                        animatedVisibilityScope = animatedContentScope,
                                        enter = fadeIn(initialAlpha = if (selectedLesson == lesson) 0f else 1f),
                                        exit = fadeOut(targetAlpha = if (selectedLesson == lesson) 0f else 1f),
                                        boundsTransform = { _, _ ->
                                            spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
                                        },
                                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                                        renderInOverlayDuringTransition = selectedLesson == lesson
                                    ),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = when(lesson.status) {
                                        "hold" -> if (isDark) Color(48, 99, 57) else Color(226, 251, 232)
                                        "canceled" -> colorScheme.errorContainer
                                        "initial" -> if (isDark) Color.DarkGray else Color.LightGray
                                        "planned" -> if (isDark) Color(38, 63, 168) else Color(222, 233, 252)
                                        else -> colorScheme.surface
                                    }.copy(0.7f)
                                ),
                                border = BorderStroke(
                                    width = 2.dp,
                                    color = if (lesson.notes.isNullOrEmpty()) {
                                        colorScheme.outline
                                    } else {
                                        lesson.notes.firstOrNull()?.type?.color?.let { Color(it.removePrefix("#").toLong(16) or 0x00000000FF000000) }
                                            ?: if (!isDark) Color(38, 63, 168) else Color(222, 233, 252)
                                    }
                                )
                            ) {
                                BoxWithConstraints(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val flip = (maxWidth.value * 1.5f) <= maxHeight.value
                                    Text(
                                        text = lesson.subject?.localId ?: "?",
                                        modifier = Modifier
                                            .rotate(if (flip) -90f else 0f)
                                            .padding(3.dp, 6.dp)
                                            .skipToLookaheadSize(),
                                        autoSize = TextAutoSize.StepBased(minFontSize = 7.5.sp, maxFontSize = 50.sp, 5.sp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            val placements = mutableMapOf<Int, LessonPlacementInfo>()
            val totalDurationInMinutes = minTime.minutesUntil(maxTime)

            if (totalDurationInMinutes <= 0) {
                return@Layout layout(constraints.maxWidth, constraints.maxHeight){}
            }

            val minutesPerPx = totalDurationInMinutes.toFloat() / constraints.maxHeight.toFloat()

            for (i in sortedLessons.indices) {
                if (placements.containsKey(i)) continue

                val lessonA = sortedLessons[i]
                val lessonAEnd = SimpleTime.parse(lessonA.time?.to ?: "00:00")

                val overlappingGroupIndices = mutableListOf(i)
                for (j in (i + 1) until sortedLessons.size) {
                    val lessonB = sortedLessons[j]
                    val lessonBStart = SimpleTime.parse(lessonB.time?.from ?: "00:00")
                    if (lessonBStart < lessonAEnd) {
                        overlappingGroupIndices.add(j)
                    }
                }

                val groupItemWidth = constraints.maxWidth / overlappingGroupIndices.size

                overlappingGroupIndices.forEachIndexed { groupIndex, lessonIndex ->
                    val lesson = sortedLessons[lessonIndex]
                    val lessonStart = SimpleTime.parse(lesson.time?.from ?: "00:00")
                    val lessonEnd = SimpleTime.parse(lesson.time?.to ?: "00:00")

                    val xPos = groupIndex * groupItemWidth
                    val yPos = (minTime.minutesUntil(lessonStart) / minutesPerPx).toInt()
                    val itemHeight = (lessonStart.minutesUntil(lessonEnd) / minutesPerPx).toInt()

                    placements[lessonIndex] = LessonPlacementInfo(xPos, yPos, groupItemWidth, itemHeight)
                }
            }

            val placeables = measurables.mapIndexed { index, measurable ->
                val p = placements[index]!!
                measurable.measure(
                    Constraints.fixed(width = p.width, height = p.height)
                )
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                placeables.forEachIndexed { index, placeable ->
                    val p = placements[index]!!
                    placeable.placeRelative(x = p.x, y = p.y)
                }
            }
        }
    }
}
