package com.hansholz.bestenotenapp.screens.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Abc
import com.composables.icons.materialsymbols.rounded.Account_circle
import com.composables.icons.materialsymbols.rounded.Alternate_email
import com.composables.icons.materialsymbols.rounded.Apartment
import com.composables.icons.materialsymbols.rounded.Architecture
import com.composables.icons.materialsymbols.rounded.Calculate
import com.composables.icons.materialsymbols.rounded.Calendar_clock
import com.composables.icons.materialsymbols.rounded.Clock_loader_80
import com.composables.icons.materialsymbols.rounded.Date_range
import com.composables.icons.materialsymbols.rounded.Demography
import com.composables.icons.materialsymbols.rounded.Electric_bolt
import com.composables.icons.materialsymbols.rounded.Event_busy
import com.composables.icons.materialsymbols.rounded.Experiment
import com.composables.icons.materialsymbols.rounded.Face
import com.composables.icons.materialsymbols.rounded.Face_2
import com.composables.icons.materialsymbols.rounded.Face_3
import com.composables.icons.materialsymbols.rounded.Face_4
import com.composables.icons.materialsymbols.rounded.Face_5
import com.composables.icons.materialsymbols.rounded.Face_6
import com.composables.icons.materialsymbols.rounded.Format_list_numbered
import com.composables.icons.materialsymbols.rounded.Globe
import com.composables.icons.materialsymbols.rounded.Health_cross
import com.composables.icons.materialsymbols.rounded.History_toggle_off
import com.composables.icons.materialsymbols.rounded.Hourglass
import com.composables.icons.materialsymbols.rounded.Info
import com.composables.icons.materialsymbols.rounded.Insights
import com.composables.icons.materialsymbols.rounded.Labs
import com.composables.icons.materialsymbols.rounded.Location_on
import com.composables.icons.materialsymbols.rounded.Menu
import com.composables.icons.materialsymbols.rounded.News
import com.composables.icons.materialsymbols.rounded.Refresh
import com.composables.icons.materialsymbols.rounded.Schedule
import com.composables.icons.materialsymbols.rounded.School
import com.composables.icons.materialsymbols.rounded.Sick
import com.composables.icons.materialsymbols.rounded.Signature
import com.composables.icons.materialsymbols.rounded.Sports
import com.composables.icons.materialsymbols.rounded.Sports_and_outdoors
import com.composables.icons.materialsymbols.rounded.Sticky_note_2
import com.composables.icons.materialsymbols.rounded.Timelapse
import com.hansholz.bestenotenapp.api.models.GradeCollection
import com.hansholz.bestenotenapp.components.GradeValueBox
import com.hansholz.bestenotenapp.components.ScatterConfig
import com.hansholz.bestenotenapp.components.ScatterItem
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.TwoToneLinearWavyProgressIndicator
import com.hansholz.bestenotenapp.components.UpdateOnNewDay
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedOutlinedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.components.scatteredIconBackground
import com.hansholz.bestenotenapp.homework.HomeworkEntry
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalHomeworkEnabled
import com.hansholz.bestenotenapp.main.LocalShowCurrentLesson
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.LocalShowNotes
import com.hansholz.bestenotenapp.main.LocalShowOnlyRelevantData
import com.hansholz.bestenotenapp.main.LocalShowYearProgress
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Fragment
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.getGreeting
import com.hansholz.bestenotenapp.utils.makeItemVisibleAndNavigate
import com.hansholz.bestenotenapp.utils.percentOfSchoolYearAt
import com.hansholz.bestenotenapp.utils.rememberCurrentSimpleTime
import com.hansholz.bestenotenapp.utils.switchPercent
import com.hansholz.bestenotenapp.utils.withRelevantLessons
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.pushpal.jetlime.LocalJetLimeStyle
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.kodein.emoji.compose.m3.TextWithNotoAnimatedEmoji
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Home(
    viewModel: ViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateToScreen: (Fragment) -> Unit,
) {
    with(sharedTransitionScope) {
        val homeViewModel = viewModel { HomeViewModel(viewModel) }

        val scope = rememberCoroutineScope()
        val vibrator = rememberVibrator()
        val layoutDirection = LocalLayoutDirection.current
        val isDark = LocalThemeIsDark.current
        val isCompactWindow =
            !currentWindowAdaptiveInfoV2()
                .windowSizeClass
                .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

        val backgroundAlpha = animateFloatAsState(if (LocalBackgroundEnabled.current.value) 0.2f else 0f, tween(750))

        val animationsEnabled by LocalAnimationsEnabled.current
        val showGreetings by LocalShowGreetings.current
        val showNewestGrades by LocalShowNewestGrades.current
        val showCurrentLesson by LocalShowCurrentLesson.current
        val showNotes by LocalShowNotes.current
        val showOnlyRelevantData by LocalShowOnlyRelevantData.current
        val showYearProgress by LocalShowYearProgress.current
        val homeworkEnabled by LocalHomeworkEnabled.current
        val homeworkRevision = viewModel.homeworkRevision.intValue
        var homework by remember { mutableStateOf(emptyList<HomeworkEntry>()) }

        val timetableYearIds by remember {
            derivedStateOf {
                buildSet {
                    viewModel.currentJournalDay.value
                        ?.lessons
                        .orEmpty()
                        .mapNotNullTo(this) { it.group?.yearId }
                    (
                        viewModel.user.value
                            ?.config
                            ?.yearId ?: viewModel.user.value
                            ?.year
                            ?.id
                    )?.let(::add)
                }
            }
        }
        LaunchedEffect(showOnlyRelevantData, timetableYearIds) {
            if (showOnlyRelevantData) viewModel.loadStudentGroups(timetableYearIds)
        }

        LaunchedEffect(viewModel.currentJournalDay.value?.date, homeworkEnabled, homeworkRevision) {
            homework =
                if (homeworkEnabled) {
                    viewModel.currentJournalDay.value
                        ?.date
                        ?.let { viewModel.getHomeworkForDate(LocalDate.parse(it)) }
                        .orEmpty()
                } else {
                    emptyList()
                }
        }

        val newestGrades by remember {
            derivedStateOf {
                viewModel
                    .startGradeCollections
                    .asSequence()
                    .filter { !it.grades.isNullOrEmpty() }
                    .distinctBy { it.id }
                    .sortedWith(compareByDescending<GradeCollection> { it.givenAt }.thenBy { it.name })
                    .take(5)
                    .toList()
            }
        }

        UpdateOnNewDay {
            homeViewModel.refreshGrades(viewModel)
            homeViewModel.refreshTimetable(viewModel)
            homeViewModel.refreshStats(viewModel)
        }

        TopAppBarScaffold(
            title = "Startseite",
            navigationIcon = {
                EnhancedIconButton(
                    onClick = {
                        scope.launch {
                            viewModel.closeOrOpenDrawer(isCompactWindow)
                        }
                    },
                ) {
                    Icon(MaterialSymbols.Rounded.Menu, null)
                }
            },
            sideMenuExpanded = viewModel.mediumExpandedDrawerState.value.isOpen,
            hazeState = viewModel.hazeBackgroundState1,
        ) { innerPadding, topAppBarBackground ->
            val lazyStaggeredGridState = rememberLazyStaggeredGridState()
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(400.dp),
                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState1),
                state = lazyStaggeredGridState,
                contentPadding =
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                        end = WindowInsets.displayCutout.asPaddingValues().calculateEndPadding(layoutDirection),
                    ),
            ) {
                item {
                    EnhancedAnimatedVisibility(
                        viewModel.user.value
                            ?.config
                            ?.yearId
                            ?.let { true } ?: false,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            EnhancedOutlinedButton(
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        viewModel.setCurrentYear()
                                        viewModel.reload()
                                        homeViewModel.refreshGrades(viewModel)
                                        homeViewModel.refreshTimetable(viewModel)
                                        homeViewModel.refreshStats(viewModel)
                                    }
                                },
                            ) {
                                Text("Zum aktuellen Schuljahr wechseln")
                            }
                        }
                    }
                }
                if (showGreetings) {
                    item {
                        EnhancedAnimatedContent(viewModel.isBesteSchuleNotReachable.value) { notReachable ->
                            if (notReachable) {
                                Text(
                                    text =
                                        "beste.schule konnte nicht erreicht werden, somit können deine Daten momentan nicht geladen werden." +
                                            "\n\nBitte überprüfe deine Internetverbindung und den Status von beste.schule." +
                                            "\n\nSollte der Fehler weiterhin auftreten, versuche dich erneut anzumelden.",
                                    modifier = Modifier.padding(20.dp),
                                    textAlign = TextAlign.Center,
                                    style = typography.bodyLarge,
                                )
                            } else {
                                var greeting by rememberSaveable { mutableStateOf("") }
                                LaunchedEffect(viewModel.user.value) {
                                    val student =
                                        viewModel.user.value
                                            ?.students
                                            ?.find { it.id.toString() == viewModel.studentId.value }
                                    if (student != null && greeting.isEmpty()) {
                                        greeting = getGreeting(student.forename ?: "du")
                                    }
                                }
                                EnhancedAnimatedContent(greeting) {
                                    val textModifier =
                                        Modifier
                                            .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                                            .padding(20.dp)
                                            .clickable(
                                                interactionSource = null,
                                                indication = null,
                                                enabled = !viewModel.isBesteSchuleNotReachable.value,
                                            ) {
                                                var newGreeting = greeting
                                                while (newGreeting == greeting) {
                                                    newGreeting =
                                                        getGreeting(
                                                            viewModel.user.value
                                                                ?.students
                                                                ?.firstOrNull()
                                                                ?.forename ?: "du",
                                                        )
                                                }
                                                greeting = newGreeting
                                                vibrator.enhancedVibrateN(EnhancedVibrations.SPIN)
                                            }
                                    if (animationsEnabled) {
                                        TextWithNotoAnimatedEmoji(
                                            text = it,
                                            modifier = textModifier,
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamilies.Schoolbell,
                                            style = typography.titleLarge,
                                        )
                                    } else {
                                        Text(
                                            text = it,
                                            modifier = textModifier,
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamilies.Schoolbell,
                                            style = typography.titleLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .scatteredIconBackground(
                                items =
                                    remember {
                                        listOf(
                                            ScatterItem.TextItem("1+"),
                                            ScatterItem.TextItem("1"),
                                            ScatterItem.TextItem("1-"),
                                            ScatterItem.TextItem("2"),
                                            ScatterItem.TextItem("3+"),
                                            ScatterItem.TextItem("4-"),
                                            ScatterItem.TextItem("5"),
                                            ScatterItem.TextItem("6"),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.School),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.School),
                                        )
                                    },
                                alpha = backgroundAlpha.value,
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                            .clickable {
                                scope.launch {
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                    makeItemVisibleAndNavigate(
                                        listState = lazyStaggeredGridState,
                                        index = if (showGreetings) 2 else 1,
                                        onNavigate = {
                                            onNavigateToScreen(Fragment.Grades)
                                        },
                                    )
                                }
                            }.enhancedSharedBounds(
                                sharedTransitionScope = sharedTransitionScope,
                                sharedContentState = rememberSharedContentState(key = "grades-card"),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                Text(
                                    text = "Noten",
                                    modifier =
                                        Modifier
                                            .align(Alignment.Center)
                                            .enhancedSharedElement(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = rememberSharedContentState(key = "grades-title"),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                            ).skipToLookaheadSize(),
                                    style = typography.headlineSmall,
                                )
                                EnhancedIconButton(
                                    onClick = {
                                        homeViewModel.refreshGrades(viewModel)
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    enabled = !homeViewModel.isGradesLoading && showNewestGrades,
                                ) {
                                    this@Column.EnhancedAnimatedVisibility(
                                        visible = !homeViewModel.isGradesLoading && showNewestGrades,
                                        enter = scaleIn(),
                                        exit = scaleOut(),
                                    ) {
                                        Icon(MaterialSymbols.Rounded.Refresh, null)
                                    }
                                }
                            }
                            if (showNewestGrades) {
                                EnhancedAnimatedContent(homeViewModel.isGradesLoading) { targetState ->
                                    if (targetState) {
                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 100.dp)) {
                                            ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                        }
                                    } else {
                                        Column {
                                            newestGrades.forEach {
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
                                                        GradeValueBox(it.grades?.getOrNull(0)?.value, viewModel.levelFor(it))
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
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .scatteredIconBackground(
                                items =
                                    remember {
                                        listOf(
                                            ScatterItem.TextItem("Ast"),
                                            ScatterItem.TextItem("Bio"),
                                            ScatterItem.TextItem("Ch"),
                                            ScatterItem.TextItem("De"),
                                            ScatterItem.TextItem("Eng"),
                                            ScatterItem.TextItem("Eth"),
                                            ScatterItem.TextItem("Geo"),
                                            ScatterItem.TextItem("Ge"),
                                            ScatterItem.TextItem("Inf"),
                                            ScatterItem.TextItem("Ku"),
                                            ScatterItem.TextItem("Ma"),
                                            ScatterItem.TextItem("Mu"),
                                            ScatterItem.TextItem("Ph"),
                                            ScatterItem.TextItem("Spo"),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Date_range),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Date_range),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Date_range),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Date_range),
                                        )
                                    },
                                config = ScatterConfig(cellSize = 85.dp, itemSizeFraction = 0.4f),
                                alpha = backgroundAlpha.value,
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                            .clickable {
                                scope.launch {
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                    makeItemVisibleAndNavigate(
                                        listState = lazyStaggeredGridState,
                                        index = if (showGreetings) 3 else 2,
                                        onNavigate = {
                                            onNavigateToScreen(Fragment.Timetable)
                                        },
                                    )
                                }
                            }.enhancedSharedBounds(
                                sharedTransitionScope = sharedTransitionScope,
                                sharedContentState = rememberSharedContentState(key = "timetable-card"),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                Text(
                                    text = "Stundenplan",
                                    modifier =
                                        Modifier
                                            .align(Alignment.Center)
                                            .enhancedSharedElement(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = rememberSharedContentState(key = "timetable-title"),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                            ).skipToLookaheadSize(),
                                    style = typography.headlineSmall,
                                )
                                EnhancedIconButton(
                                    onClick = {
                                        homeViewModel.refreshTimetable(viewModel)
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    enabled = !homeViewModel.isTimetableLoading && showCurrentLesson,
                                ) {
                                    this@Column.EnhancedAnimatedVisibility(
                                        visible = !homeViewModel.isTimetableLoading && showCurrentLesson,
                                        enter = scaleIn(),
                                        exit = scaleOut(),
                                    ) {
                                        Icon(MaterialSymbols.Rounded.Refresh, null)
                                    }
                                }
                            }
                            if (showCurrentLesson) {
                                EnhancedAnimatedContent(homeViewModel.isTimetableLoading) { targetState ->
                                    if (targetState) {
                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 100.dp)) {
                                            ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                        }
                                    } else {
                                        val currentJournalDay =
                                            viewModel.currentJournalDay.value?.let {
                                                if (showOnlyRelevantData) {
                                                    it.withRelevantLessons(viewModel.studentGroupsByYear)
                                                } else {
                                                    it
                                                }
                                            }
                                        if (!currentJournalDay
                                                ?.lessons
                                                .isNullOrEmpty()
                                        ) {
                                            val lessons = currentJournalDay.lessons
                                            val groupedLessons = remember(lessons) { lessons.sortedBy { it.nr }.groupBy { it.nr } }
                                            val maxLessonNr = remember(lessons) { lessons.maxOf { it.nr.toInt() } }
                                            val currentTime by rememberCurrentSimpleTime()
                                            Column(Modifier.padding(10.dp).padding(start = 5.dp)) {
                                                CompositionLocalProvider(
                                                    LocalJetLimeStyle provides
                                                        JetLimeDefaults
                                                            .columnStyle(
                                                                lineBrush = JetLimeDefaults.lineSolidBrush(colorScheme.primary.copy(0.7f)),
                                                            ),
                                                ) {
                                                    groupedLessons.forEach { groupLessons ->
                                                        val firstLesson = groupLessons.value[0]
                                                        val position = EventPosition.dynamic(firstLesson.nr.toInt() - 1, maxLessonNr)
                                                        val lessonTimeStart = SimpleTime.parse(firstLesson.time?.from ?: "00:00")
                                                        val lessonTimeEnd = SimpleTime.parse(firstLesson.time?.to ?: "00:00")
                                                        @OptIn(ExperimentalComposeApi::class)
                                                        JetLimeExtendedEvent(
                                                            style =
                                                                JetLimeEventDefaults.eventStyle(
                                                                    position = position,
                                                                    pointAnimation =
                                                                        if (currentTime in
                                                                            lessonTimeStart..lessonTimeEnd
                                                                        ) {
                                                                            JetLimeEventDefaults.pointAnimation(targetValue = 1.4f)
                                                                        } else {
                                                                            null
                                                                        },
                                                                    pointType = if (lessonTimeStart <= currentTime) EventPointType.Default else EventPointType.EMPTY,
                                                                    pointColor =
                                                                        if (groupLessons.value.size > 1) {
                                                                            colorScheme.surface
                                                                        } else {
                                                                            when (firstLesson.status) {
                                                                                "hold" -> if (isDark) Color(48, 99, 57) else Color(226, 251, 232)
                                                                                "canceled" -> colorScheme.errorContainer
                                                                                "initial" -> if (isDark) Color.DarkGray else Color.LightGray
                                                                                "planned" -> if (isDark) Color(38, 63, 168) else Color(222, 233, 252)
                                                                                else -> colorScheme.surface
                                                                            }
                                                                        },
                                                                ),
                                                            additionalContent = {
                                                                Box(Modifier.clip(ClamShell.toShape()).background(colorScheme.primaryContainer)) {
                                                                    Text(
                                                                        text =
                                                                            groupLessons.value
                                                                                .flatMap { it.rooms.orEmpty() }
                                                                                .map { it.localId }
                                                                                .toSet()
                                                                                .joinToString()
                                                                                .ifEmpty { "?" },
                                                                        modifier = Modifier.width(60.dp).padding(vertical = 2.dp),
                                                                        color = colorScheme.onPrimaryContainer,
                                                                        textAlign = TextAlign.Center,
                                                                    )
                                                                }
                                                            },
                                                        ) {
                                                            Column(Modifier.padding(start = 5.dp)) {
                                                                Text(
                                                                    text =
                                                                        groupLessons.value
                                                                            .map { it.subject?.name ?: "?" }
                                                                            .toSet()
                                                                            .joinToString(),
                                                                    color = if (currentTime in lessonTimeStart..lessonTimeEnd) colorScheme.primary else Color.Unspecified,
                                                                )
                                                                groupLessons.value.flatMap { it.notes.orEmpty() }.forEach {
                                                                    Text(
                                                                        text =
                                                                            (it.type?.name?.replace("Substitution Plan", "Vertretungsplan") ?: "?") +
                                                                                ": ${it.description ?: "Keine Beschreibung"}",
                                                                        modifier = Modifier.padding(vertical = 5.dp),
                                                                        color =
                                                                            if (currentTime in
                                                                                lessonTimeStart..lessonTimeEnd
                                                                            ) {
                                                                                colorScheme.primary
                                                                            } else {
                                                                                Color.Unspecified
                                                                            },
                                                                        style = typography.bodyMedium,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (showNotes || homework.isNotEmpty()) {
                                                    val notes =
                                                        currentJournalDay
                                                            .notes
                                                            ?.filter { it.description != null }
                                                    if (!notes.isNullOrEmpty() || homework.isNotEmpty()) Spacer(Modifier.height(5.dp))
                                                    if (showNotes) {
                                                        notes?.forEach { note ->
                                                            Column {
                                                                HorizontalDivider(
                                                                    Modifier.fillMaxWidth().padding(top = 5.dp),
                                                                    2.dp,
                                                                    colorScheme.outline,
                                                                )
                                                                Row(
                                                                    modifier = Modifier.padding(top = 5.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                ) {
                                                                    Icon(MaterialSymbols.Rounded.News, null, Modifier.padding(end = 10.dp))
                                                                    Text(note.description ?: "Keine Beschreibung vorhanden")
                                                                }
                                                            }
                                                        }
                                                    }
                                                    homework.forEach { entry ->
                                                        Column {
                                                            HorizontalDivider(Modifier.fillMaxWidth().padding(top = 5.dp), 2.dp, colorScheme.outline)
                                                            Row(
                                                                modifier = Modifier.padding(top = 5.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                Icon(
                                                                    MaterialSymbols.Rounded.News,
                                                                    null,
                                                                    Modifier.padding(end = 10.dp),
                                                                    tint = colorScheme.error,
                                                                )
                                                                Text(
                                                                    if (entry.subjectName.isNullOrBlank()) {
                                                                        entry.title
                                                                    } else {
                                                                        "${entry.subjectName}: ${entry.title}"
                                                                    },
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
                            Text(
                                text = "Tippen, um deinen wöchentlichen Stundenplan zu sehen",
                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .scatteredIconBackground(
                                items =
                                    remember {
                                        listOf(
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Experiment),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Labs),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Electric_bolt),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Calculate),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Globe),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Sports),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Sports_and_outdoors),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Architecture),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Abc),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face_2),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face_3),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face_4),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face_5),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Face_6),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Demography),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Demography),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Demography),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Demography),
                                        )
                                    },
                                alpha = backgroundAlpha.value,
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                            .clickable {
                                scope.launch {
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                    makeItemVisibleAndNavigate(
                                        listState = lazyStaggeredGridState,
                                        index = if (showGreetings) 4 else 3,
                                        onNavigate = {
                                            onNavigateToScreen(Fragment.SubjectsAndTeachers)
                                        },
                                    )
                                }
                            }.enhancedSharedBounds(
                                sharedTransitionScope = sharedTransitionScope,
                                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-card"),
                                animatedVisibilityScope = animatedVisibilityScope,
                            ),
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                Text(
                                    text = "Fächer und Lehrer",
                                    modifier =
                                        Modifier
                                            .align(Alignment.Center)
                                            .enhancedSharedElement(
                                                sharedTransitionScope = sharedTransitionScope,
                                                sharedContentState = rememberSharedContentState(key = "subjects-and-teachers-title"),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                            ).skipToLookaheadSize(),
                                    fontFamily = FontFamilies.KeaniaOne,
                                    style = typography.headlineSmall,
                                )
                                EnhancedIconButton(onClick = {}, enabled = false) {}
                            }
                            Text(
                                text = "Tippen, um einen Überblick über Fächer und Lehrer zu bekommen",
                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    Box(
                        Modifier
                            .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .scatteredIconBackground(
                                items =
                                    remember {
                                        listOf(
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Sick),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Health_cross),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Event_busy),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.History_toggle_off),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Signature),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Sticky_note_2),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Insights),
                                            ScatterItem.IconItem(MaterialSymbols.Rounded.Insights),
                                        )
                                    },
                                alpha = backgroundAlpha.value,
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                            .clickable {
                                homeViewModel.isStatsDialogShown = true
                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                            },
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                Text(
                                    text = "Jahresinformationen",
                                    modifier = Modifier.align(Alignment.Center),
                                    style = typography.headlineSmall,
                                )
                                EnhancedIconButton(
                                    onClick = {
                                        homeViewModel.refreshStats(viewModel)
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    enabled = !homeViewModel.isStatsLoading && showYearProgress,
                                ) {
                                    this@Column.EnhancedAnimatedVisibility(
                                        visible = !homeViewModel.isStatsLoading && showYearProgress,
                                        enter = scaleIn(),
                                        exit = scaleOut(),
                                    ) {
                                        Icon(MaterialSymbols.Rounded.Refresh, null)
                                    }
                                }
                            }
                            if (showYearProgress) {
                                EnhancedAnimatedContent(homeViewModel.isStatsLoading || viewModel.intervals.isEmpty()) {
                                    if (it) {
                                        LinearWavyProgressIndicator(Modifier.height(40.dp).fillMaxWidth().padding(10.dp))
                                    } else {
                                        val firstIntervalFrom = remember(viewModel.intervals) { LocalDate.parse(viewModel.intervals[0].from) }
                                        val firstIntervalTo = remember(viewModel.intervals) { LocalDate.parse(viewModel.intervals[0].to) }
                                        val secondIntervalFrom = remember(viewModel.intervals) { LocalDate.parse(viewModel.intervals[1].from) }
                                        val secondIntervalTo = remember(viewModel.intervals) { LocalDate.parse(viewModel.intervals[1].to) }
                                        val progress =
                                            remember(firstIntervalFrom, firstIntervalTo, secondIntervalFrom, secondIntervalTo) {
                                                percentOfSchoolYearAt(firstIntervalFrom, firstIntervalTo, secondIntervalFrom, secondIntervalTo)
                                            }
                                        val split =
                                            remember(firstIntervalFrom, firstIntervalTo, secondIntervalFrom, secondIntervalTo) {
                                                switchPercent(firstIntervalFrom, firstIntervalTo, secondIntervalFrom, secondIntervalTo)
                                            }
                                        Column {
                                            TwoToneLinearWavyProgressIndicator(
                                                progress = progress,
                                                split = split,
                                                firstColor = colorScheme.primary,
                                                secondColor = colorScheme.inversePrimary,
                                                modifier = Modifier.height(40.dp).fillMaxWidth().padding(10.dp),
                                            )
                                            Text(
                                                text = "Du hast aktuell ${(progress * 100).roundToInt()}% des Schuljahres geschafft",
                                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = "Tippen, um Informationen zum aktuellen Schuljahr zu erhalten",
                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                if (!viewModel.isDemoAccount.value) {
                    item {
                        Box(
                            Modifier
                                .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                                .scatteredIconBackground(
                                    items =
                                        remember {
                                            listOf(
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Clock_loader_80),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Timelapse),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Hourglass),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Schedule),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Calendar_clock),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Calendar_clock),
                                            )
                                        },
                                    alpha = backgroundAlpha.value,
                                ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                                .clickable {
                                    homeViewModel.isTimesDialogShown = true
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                },
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                    Text(
                                        text = "Unterrichtszeiten",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = typography.headlineSmall,
                                    )
                                }
                                Text(
                                    text = "Tippen, um die Unterrichtszeiten deiner Schule anschauen zu können",
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
                if (viewModel.user.value != null) {
                    item {
                        Box(
                            Modifier
                                .then(if (animationsEnabled) Modifier.animateItem().animateContentSize() else Modifier)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                                .scatteredIconBackground(
                                    items =
                                        remember {
                                            listOf(
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Account_circle),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Info),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Alternate_email),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Format_list_numbered),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Location_on),
                                                ScatterItem.IconItem(MaterialSymbols.Rounded.Apartment),
                                            )
                                        },
                                    alpha = backgroundAlpha.value,
                                ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(24.dp))
                                .clickable {
                                    homeViewModel.isAccountSchoolDialogShown = true
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                },
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                Box(Modifier.fillMaxWidth().padding(10.dp).padding(top = 10.dp)) {
                                    Text(
                                        text = "Account- und Schuldaten",
                                        modifier = Modifier.align(Alignment.Center),
                                        style = typography.headlineSmall,
                                    )
                                }
                                Text(
                                    text = "Tippen, um deine Account- und Schuldaten einsehen zu können",
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
                if (!viewModel.isDemoAccount.value) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 20.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            EnhancedOutlinedButton(
                                onClick = {
                                    homeViewModel.isYearSelectionDialogShown = true
                                },
                            ) {
                                Text("Schuljahr ${viewModel.user.value?.year?.name}")
                            }
                        }
                    }
                }
            }
            topAppBarBackground(innerPadding.calculateTopPadding())
        }

        StatsDialog(viewModel, homeViewModel)
        TimesDialog(viewModel, homeViewModel)
        AccountSchoolDialog(viewModel, homeViewModel)
        YearSelectionDialog(viewModel, homeViewModel)
    }
}
