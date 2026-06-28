package com.hansholz.bestenotenapp.screens.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Close
import com.hansholz.bestenotenapp.api.models.Absence
import com.hansholz.bestenotenapp.api.models.JournalLesson
import com.hansholz.bestenotenapp.api.models.JournalWeek
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.components.enhanced.enhancedSharedBounds
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.theme.LocalBlurEnabled
import com.hansholz.bestenotenapp.theme.LocalThemeIsDark
import com.hansholz.bestenotenapp.utils.SimpleTime
import com.hansholz.bestenotenapp.utils.formateDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import top.ltfan.multihaptic.compose.rememberVibrator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalTime::class,
)
@Composable
fun WeekScheduleView(
    week: JournalWeek?,
    absences: List<Absence>,
    lessonPopupShown: MutableState<Boolean>,
    isCurrentPage: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    captureOnly: Boolean = false,
) {
    val vibrator = rememberVibrator()
    val isDark = LocalThemeIsDark.current
    val blurEnabled = LocalBlurEnabled.current
    val showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

    val days = week?.days ?: return

    val allLessons =
        remember(days) {
            days
                .map {
                    it.copy(
                        lessons =
                            it.lessons?.mapIndexed { index, lesson ->
                                val startTime = SimpleTime.parse("07:30").plus(50 * index)
                                if (lesson.time?.from == null || lesson.time.to == null) {
                                    lesson.copy(time = lesson.time?.copy(from = startTime.toString(), to = startTime.plus(45).toString()))
                                } else {
                                    lesson
                                }
                            },
                    )
                }.flatMap { it.lessons.orEmpty() }
        }

    if (allLessons.isEmpty()) return

    val minTimeHour = allLessons.minOfOrNull { SimpleTime.parse(it.time?.from ?: "23:59") }
    val latestLessonEnd = allLessons.maxOfOrNull { SimpleTime.parse(it.time?.to ?: "00:00") }

    var selectedLesson by remember { mutableStateOf<JournalLesson?>(null) }
    var selectedDay by remember {
        mutableStateOf(
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date,
        )
    }

    val popupVisible = lessonPopupShown.value && selectedLesson != null
    val popupVisibilityState =
        remember {
            SeekableTransitionState(false)
        }
    val popupTransition = rememberTransition(popupVisibilityState, label = "TimetableLessonPopup")
    var isBackInProgress by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(popupVisible, isBackInProgress) {
        if (!isBackInProgress) {
            popupVisibilityState.animateTo(popupVisible)
        }
    }
    val popupBoundsTransform =
        remember {
            BoundsTransform { _, _ ->
                spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                )
            }
        }
    val animatedScheduleScale by animateFloatAsState(
        targetValue = if (popupVisible) 0.985f else 1f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        label = "TimetablePopupContentScale",
    )
    val effectiveScheduleScale =
        if (isBackInProgress) {
            0.985f + ((1f - 0.985f) * backProgress)
        } else {
            animatedScheduleScale
        }

    @Suppress("DEPRECATION")
    PredictiveBackHandler(enabled = isCurrentPage && popupVisible) { progressFlow ->
        var receivedProgress = false
        try {
            popupVisibilityState.snapTo(true)
            progressFlow.collect { event ->
                if (!receivedProgress) {
                    receivedProgress = true
                    isBackInProgress = true
                    backProgress = 0f
                    popupVisibilityState.seekTo(0f, false)
                }
                backProgress = event.progress.coerceIn(0f, 1f) * 0.2f
                popupVisibilityState.seekTo(backProgress, false)
            }

            if (receivedProgress) {
                backProgress = 1f
                lessonPopupShown.value = false
                popupVisibilityState.animateTo(false)
            } else {
                lessonPopupShown.value = false
            }
            isBackInProgress = false
            backProgress = 0f
        } catch (_: CancellationException) {
            withContext(NonCancellable) {
                val initialBackProgress = backProgress
                val rollbackDurationNanos = 160_000_000L
                var startTimeNanos: Long? = null
                do {
                    val frameTimeNanos = withFrameNanos { it }
                    val startTime = startTimeNanos ?: frameTimeNanos.also { startTimeNanos = it }
                    val fraction = ((frameTimeNanos - startTime).toFloat() / rollbackDurationNanos).coerceIn(0f, 1f)
                    val easedFraction = 1f - ((1f - fraction) * (1f - fraction))
                    val rollbackProgress = initialBackProgress * (1f - easedFraction)
                    backProgress = rollbackProgress
                    popupVisibilityState.seekTo(rollbackProgress, false)
                } while (fraction < 1f)
                popupVisibilityState.snapTo(true)
            }
            isBackInProgress = false
            backProgress = 0f
        }
    }

    SharedTransitionLayout(
        modifier = Modifier,
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .enhancedHazeEffect(
                            blurRadius =
                                animateDpAsState(
                                    targetValue = if (popupVisible) 10.dp else 0.dp,
                                    animationSpec = tween(if (popupVisible) 180 else 140),
                                    label = "TimetablePopupBlurRadius",
                                ).value,
                        ).graphicsLayer {
                            scaleX = effectiveScheduleScale
                            scaleY = effectiveScheduleScale
                        }.padding(contentPadding)
                        .then(modifier),
            ) {
                days.forEachIndexed { dayIndex, day ->
                    if (!day.lessons.isNullOrEmpty()) {
                        val currentDate = LocalDate.parse(days.firstOrNull()?.date ?: "2000-01-01").plus(dayIndex, DateTimeUnit.DAY)
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            DayHeader(
                                date = currentDate,
                                notes =
                                    if (captureOnly) {
                                        null
                                    } else {
                                        day.notes?.filter { it.description != null }
                                    },
                            )
                            DailyScheduleLayout(
                                lessons = day.lessons,
                                absences = absences,
                                date = currentDate,
                                modifier = Modifier.fillMaxHeight(),
                                minTime = minTimeHour ?: SimpleTime.parse("7:00"),
                                maxTime = latestLessonEnd ?: SimpleTime.parse("18:00"),
                                sharedTransitionScope = this@SharedTransitionLayout,
                                selectedLesson = selectedLesson,
                                popupTransition = popupTransition,
                                popupBoundsTransform = popupBoundsTransform,
                            ) { lesson ->
                                if (enabled) {
                                    vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                    selectedLesson = lesson
                                    selectedDay = currentDate
                                    lessonPopupShown.value = true
                                }
                            }
                        }
                    }
                }
            }

            popupTransition.AnimatedVisibility(
                visible = { it },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(
                            if (lessonPopupShown.value) {
                                Modifier.clickable(null, null) {
                                    lessonPopupShown.value = false
                                    vibrator.enhancedVibrateN(EnhancedVibrations.QUICK_FALL)
                                }
                            } else {
                                Modifier
                            },
                        ),
                enter = fadeIn(animationSpec = tween(120)),
                exit = fadeOut(animationSpec = tween(100)),
            ) {
                Box(
                    Modifier.fillMaxSize(),
                ) {
                    OutlinedCard(
                        modifier =
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .widthIn(max = 350.dp)
                                .align(Alignment.Center)
                                .padding(contentPadding)
                                .padding(10.dp)
                                .enhancedSharedBounds(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    sharedContentState = rememberSharedContentState(selectedLesson ?: ""),
                                    animatedVisibilityScope = this@AnimatedVisibility,
                                    boundsTransform = popupBoundsTransform,
                                ).clickable(null, null) {},
                        shape = RoundedCornerShape(24.dp),
                        colors =
                            CardDefaults.outlinedCardColors(
                                containerColor =
                                    when (selectedLesson?.status) {
                                        "hold" -> if (isDark) Color(48, 99, 57) else Color(226, 251, 232)
                                        "canceled" -> colorScheme.errorContainer
                                        "initial" -> if (isDark) Color.DarkGray else Color.LightGray
                                        "planned" -> if (isDark) Color(38, 63, 168) else Color(160, 182, 238)
                                        else -> colorScheme.surface
                                    }.copy(if (blurEnabled.value) 0.9f else 1f),
                            ),
                        border = BorderStroke(2.dp, colorScheme.outline),
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            EnhancedIconButton(
                                onClick = {
                                    lessonPopupShown.value = false
                                },
                                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                                isExpressive = false,
                            ) {
                                Icon(MaterialSymbols.Rounded.Close, null)
                            }
                            Text(
                                text = selectedLesson?.subject?.localId ?: "?",
                                modifier = Modifier.align(Alignment.Center).skipToLookaheadSize(),
                                color =
                                    if (absences.any {
                                            LocalDate.parse(it.from.take(10)) <= selectedDay &&
                                                LocalDate.parse(it.to.take(10)) >= selectedDay &&
                                                SimpleTime.parse(it.from.takeLast(8)) <= SimpleTime.parse(selectedLesson?.time?.from ?: "00:00") &&
                                                SimpleTime.parse(it.to.takeLast(8)) >= SimpleTime.parse(selectedLesson?.time?.to ?: "23:59")
                                        }
                                    ) {
                                        colorScheme.error
                                    } else {
                                        Color.Unspecified
                                    },
                                style = typography.headlineLarge,
                            )
                        }
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = selectedLesson?.subject?.name ?: "Unbekanntes Fach",
                                    style = typography.headlineSmall,
                                )
                            },
                            overlineContent = {
                                Text(
                                    text =
                                        "${when (selectedDay.dayOfWeek) {
                                            DayOfWeek.MONDAY -> "Montag"
                                            DayOfWeek.TUESDAY -> "Dienstag"
                                            DayOfWeek.WEDNESDAY -> "Mittwoch"
                                            DayOfWeek.THURSDAY -> "Donnerstag"
                                            DayOfWeek.FRIDAY -> "Freitag"
                                            DayOfWeek.SATURDAY -> "Samstag"
                                            DayOfWeek.SUNDAY -> "Sonntag"
                                        }}, ${formateDate(selectedDay.toString())}\n${selectedLesson?.nr ?: "?"}. Stunde" +
                                            if (days
                                                    .flatMap { it.lessons.orEmpty() }
                                                    .find { it == selectedLesson }
                                                    ?.time
                                                    ?.from != null
                                            ) {
                                                " (${selectedLesson?.time?.from} - ${selectedLesson?.time?.to})"
                                            } else {
                                                ""
                                            },
                                )
                            },
                            supportingContent = {
                                Text(
                                    when (selectedLesson?.status) {
                                        "hold" -> "gehalten"
                                        "canceled" -> "abgesagt"
                                        "initial" -> "initial"
                                        "planned" -> "geplant"
                                        else -> "unbekannter Status"
                                    },
                                )
                            },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = { Text(selectedLesson?.group?.name?.ifEmpty { "Unbekannt" } ?: "Unbekannt") },
                            overlineContent = { Text("Klasse/Gruppe") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = {
                                Text(
                                    selectedLesson
                                        ?.teachers
                                        ?.joinToString {
                                            (
                                                (
                                                    if (showTeachersWithFirstname) {
                                                        it.forename
                                                    } else {
                                                        it.forename?.take(
                                                            1,
                                                        ) + "."
                                                    }
                                                ) + " " + it.name
                                            )
                                        }?.ifEmpty { "Unbekannt" }
                                        ?: "Unbekannt",
                                )
                            },
                            overlineContent = { Text("Lehrer/-in(nen)") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                        )
                        HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                        ListItem(
                            headlineContent = { Text(selectedLesson?.rooms?.joinToString { it.localId }?.ifEmpty { "Unbekannt" } ?: "Unbekannt") },
                            overlineContent = { Text("Raum/Räume") },
                            modifier = Modifier.skipToLookaheadSize(),
                            colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                        )
                        selectedLesson?.notes?.forEach { note ->
                            HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                            ListItem(
                                headlineContent = { Text(note.description ?: "Leer") },
                                overlineContent = { Text(note.type?.name?.replace("Substitution Plan", "Vertretungsplan") ?: "Unbenannte Notiz") },
                                trailingContent = {
                                    note.type?.color?.let {
                                        Box(
                                            Modifier.size(15.dp).clip(CircleShape).background(
                                                Color(
                                                    it.removePrefix("#").toLong(16) or 0x00000000FF000000,
                                                ),
                                            ),
                                        )
                                    }
                                },
                                modifier = Modifier.skipToLookaheadSize(),
                                colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                            )
                        }
                        absences
                            .filter {
                                LocalDate.parse(it.from.take(10)) <= selectedDay &&
                                    LocalDate.parse(it.to.take(10)) >= selectedDay &&
                                    SimpleTime.parse(it.from.takeLast(8)) <= SimpleTime.parse(selectedLesson?.time?.from ?: "00:00") &&
                                    SimpleTime.parse(it.to.takeLast(8)) >= SimpleTime.parse(selectedLesson?.time?.to ?: "23:59")
                            }.forEach { absence ->
                                val dialogShown = remember { mutableStateOf(false) }
                                HorizontalDivider(thickness = 2.dp, color = colorScheme.outline)
                                ListItem(
                                    headlineContent = { Text(absence.type.name, color = colorScheme.error) },
                                    overlineContent = { Text("Abwesenheit durch") },
                                    modifier =
                                        Modifier
                                            .clickable {
                                                dialogShown.value = true
                                                vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                                            }.skipToLookaheadSize(),
                                    colors = ListItemDefaults.colors(colorScheme.surfaceContainer.copy(0.5f)),
                                )
                                AbsenceInfoDialog(dialogShown, absence)
                            }
                    }
                }
            }
        }
    }
}
