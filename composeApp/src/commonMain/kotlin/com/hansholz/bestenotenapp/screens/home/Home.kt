@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes.Companion.ClamShell
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
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
import androidx.lifecycle.viewmodel.compose.viewModel
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.grades
import bestenotenapp.composeapp.generated.resources.subjectsAndTeachers
import bestenotenapp.composeapp.generated.resources.timetable
import com.hansholz.bestenotenapp.components.GradeValueBox
import com.hansholz.bestenotenapp.components.TopAppBarScaffold
import com.hansholz.bestenotenapp.components.UpdateOnNewDay
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedElement
import com.hansholz.bestenotenapp.components.repeatingBackground
import com.hansholz.bestenotenapp.main.LocalBackgroundEnabled
import com.hansholz.bestenotenapp.main.LocalShowCurrentLesson
import com.hansholz.bestenotenapp.main.LocalShowGreetings
import com.hansholz.bestenotenapp.main.LocalShowNewestGrades
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.navigation.Fragment
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.hansholz.bestenotenapp.utils.formateDate
import com.hansholz.bestenotenapp.utils.getGreeting
import com.hansholz.bestenotenapp.utils.rememberCurrentSimpleTime
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import com.pushpal.jetlime.LocalJetLimeStyle
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
    onNavigateToScreen: (Fragment) -> Unit,
) {
    with(sharedTransitionScope) {
        val homeViewModel = viewModel { HomeViewModel(viewModel) }

        val scope = rememberCoroutineScope()
        val isDark = LocalThemeIsDark.current
        val hapticFeedback = LocalHapticFeedback.current
        val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

        val backgroundAlpha = animateFloatAsState(if (LocalBackgroundEnabled.current.value) 0.2f else 0f, tween(750))

        val showGreetings by LocalShowGreetings.current
        val showNewestGrades by LocalShowNewestGrades.current
        val showCurrentLesson by LocalShowCurrentLesson.current

        UpdateOnNewDay {
            homeViewModel.refreshTimetable(viewModel)
        }

        TopAppBarScaffold(
            title = "Startseite",
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
            hazeState = viewModel.hazeBackgroundState1,
        ) { innerPadding, topAppBarBackground ->
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(400.dp),
                modifier = Modifier.hazeSource(viewModel.hazeBackgroundState1),
                contentPadding = innerPadding,
            ) {
                if (showGreetings) {
                    item {
                        AnimatedContent(viewModel.isBesteSchuleNotReachable.value) { notReachable ->
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
                                AnimatedContent(greeting) {
                                    TextWithNotoAnimatedEmoji(
                                        text = it,
                                        modifier =
                                            Modifier
                                                .animateItem()
                                                .animateContentSize()
                                                .padding(20.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null,
                                                    enabled = !viewModel.isBesteSchuleNotReachable.value,
                                                ) {
                                                    greeting =
                                                        getGreeting(
                                                            viewModel.user.value
                                                                ?.students
                                                                ?.firstOrNull()
                                                                ?.forename ?: "du",
                                                        )
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                },
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamilies.Schoolbell(),
                                        style = typography.titleLarge,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    val imageBitmap = imageResource(Res.drawable.grades)
                    Box(
                        Modifier
                            .animateItem()
                            .animateContentSize()
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .repeatingBackground(
                                imageBitmap = imageBitmap,
                                alpha = backgroundAlpha.value,
                                scale = 0.75f,
                                offset = remember { Offset(x = Random.nextFloat() * imageBitmap.width, y = 0f) },
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(12.dp))
                            .clickable {
                                onNavigateToScreen(Fragment.Grades)
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                    this@Column.AnimatedVisibility(
                                        visible = !homeViewModel.isGradesLoading && showNewestGrades,
                                        enter = scaleIn(),
                                        exit = scaleOut(),
                                    ) {
                                        Icon(Icons.Outlined.Refresh, null)
                                    }
                                }
                            }
                            if (showNewestGrades) {
                                AnimatedContent(homeViewModel.isGradesLoading) { targetState ->
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
                                                .toSet()
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
                                                            GradeValueBox(it.grades?.getOrNull(0)?.value)
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
                    val imageBitmap = imageResource(Res.drawable.timetable)
                    Box(
                        Modifier
                            .animateItem()
                            .animateContentSize()
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .repeatingBackground(
                                imageBitmap = imageBitmap,
                                alpha = backgroundAlpha.value,
                                scale = 0.6f,
                                offset = remember { Offset(x = Random.nextFloat() * imageBitmap.width, y = -50f) },
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(12.dp))
                            .clickable {
                                onNavigateToScreen(Fragment.Timetable)
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                    this@Column.AnimatedVisibility(
                                        visible = !homeViewModel.isTimetableLoading && showCurrentLesson,
                                        enter = scaleIn(),
                                        exit = scaleOut(),
                                    ) {
                                        Icon(Icons.Outlined.Refresh, null)
                                    }
                                }
                            }
                            if (showCurrentLesson) {
                                AnimatedContent(homeViewModel.isTimetableLoading) { targetState ->
                                    if (targetState) {
                                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 100.dp)) {
                                            ContainedLoadingIndicator(Modifier.align(Alignment.Center))
                                        }
                                    } else {
                                        if (!viewModel.currentJournalDay.value
                                                ?.lessons
                                                .isNullOrEmpty()
                                        ) {
                                            val lessons = viewModel.currentJournalDay.value!!.lessons!!
                                            Column(Modifier.padding(10.dp).padding(start = 5.dp)) {
                                                CompositionLocalProvider(
                                                    LocalJetLimeStyle provides
                                                        JetLimeDefaults
                                                            .columnStyle(
                                                                lineBrush = JetLimeDefaults.lineSolidBrush(colorScheme.primary.copy(0.7f)),
                                                            ),
                                                ) {
                                                    lessons.sortedBy { it.nr }.groupBy { it.nr }.forEach { groupLessons ->
                                                        val firstLesson = groupLessons.value[0]
                                                        val position = EventPosition.dynamic(firstLesson.nr.toInt() - 1, lessons.maxOf { it.nr.toInt() })
                                                        val lessonTimeStart = SimpleTime.parse(firstLesson.time?.from ?: "00:00")
                                                        val lessonTimeEnd = SimpleTime.parse(firstLesson.time?.to ?: "00:00")
                                                        val currentTime by rememberCurrentSimpleTime()
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
                                                                        color = if (currentTime in lessonTimeStart..lessonTimeEnd) colorScheme.primary else Color.Unspecified,
                                                                        style = typography.bodyMedium,
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
                            Text(
                                text = "Tippen, um deinen wöchentlichen Stundenplan zu sehen",
                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                item {
                    val imageBitmap = imageResource(Res.drawable.subjectsAndTeachers)
                    Box(
                        Modifier
                            .animateItem()
                            .animateContentSize()
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.surfaceContainerHighest.copy(0.7f))
                            .repeatingBackground(
                                imageBitmap = imageBitmap,
                                alpha = backgroundAlpha.value,
                                scale = 0.6f,
                                offset = remember { Offset(x = Random.nextFloat() * imageBitmap.width, y = -100f) },
                            ).border(BorderStroke(2.dp, colorScheme.outline), RoundedCornerShape(12.dp))
                            .clickable {
                                onNavigateToScreen(Fragment.SubjectsAndTeachers)
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
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
                                    fontFamily = FontFamilies.KeaniaOne(),
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
            }
            topAppBarBackground(innerPadding.calculateTopPadding())
        }
    }
}
