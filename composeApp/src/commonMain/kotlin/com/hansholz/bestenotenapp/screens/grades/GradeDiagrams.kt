package com.hansholz.bestenotenapp.screens.grades

import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedContent
import com.hansholz.bestenotenapp.components.enhanced.EnhancedAnimatedVisibility
import com.hansholz.bestenotenapp.components.enhanced.EnhancedButton
import com.hansholz.bestenotenapp.components.enhanced.EnhancedCheckbox
import com.hansholz.bestenotenapp.components.enhanced.EnhancedIconButton
import com.hansholz.bestenotenapp.components.enhanced.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.utils.normalizeGrade
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

@OptIn(ExperimentalKoalaPlotApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GradeDiagrams(
    viewModel: ViewModel,
    gradesViewModel: GradesViewModel,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val hazeState = rememberHazeState()

    Box {
        val firstLazyListState = rememberLazyListState()
        val secondLazyListState = rememberLazyListState()
        val lazyListState = if (gradesViewModel.analyzeYears) secondLazyListState else firstLazyListState

        val filteredGrades =
            viewModel.gradeCollections
                .toSet()
                .filter { gradesViewModel.selectedYears.map { it.id }.contains(it.interval?.yearId) }
                .filter {
                    !it.grades.isNullOrEmpty() &&
                        it.grades
                            .firstOrNull()
                            ?.value
                            ?.take(1)
                            ?.toIntOrNull() != null
                }
        val allFilteredGrades =
            viewModel.gradeCollections
                .asSequence()
                .filter { it.interval?.yearId != null && !it.grades.isNullOrEmpty() }

        KoalaPlotTheme(animationSpec = if (LocalAnimationsEnabled.current.value) spring(2f) else snap()) {
            EnhancedAnimatedContent(gradesViewModel.analyzeYears) { analyzeYears ->
                if (!analyzeYears) {
                    LazyColumn(
                        modifier = Modifier.hazeSource(hazeState),
                        state = firstLazyListState,
                        contentPadding = PaddingValues(top = gradesViewModel.titleHeight, bottom = gradesViewModel.closeBarHeight),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            val grades =
                                filteredGrades
                                    .filter { !gradesViewModel.filterSubjects || !gradesViewModel.deselectedSubjects.contains(it.subject?.name) }
                                    .map {
                                        it.grades!![0]
                                            .value
                                            .take(1)
                                            .toIntOrNull() ?: 0
                                    }.sorted()

                            if (grades.isNotEmpty()) {
                                val counts: Map<Int, Int> = grades.groupingBy { it }.eachCount()

                                val minX = counts.keys.minOrNull()!!
                                val maxX = counts.keys.maxOrNull()!!

                                val completeGrades: List<Pair<Int, Int>> = (minX..maxX).map { x -> x to (counts[x] ?: 0) }

                                val barChartEntries =
                                    buildList {
                                        completeGrades.forEach { (x, c) ->
                                            add(
                                                DefaultVerticalBarPlotEntry(
                                                    x.toFloat(),
                                                    DefaultVerticalBarPosition(0f, c.toFloat()),
                                                ),
                                            )
                                        }
                                    }

                                val yMax = maxOf(1, completeGrades.maxOf { it.second })

                                XYGraph(
                                    xAxisModel =
                                        FloatLinearAxisModel(
                                            (minX - 1).toFloat()..(maxX + 1).toFloat(),
                                            minimumMajorTickIncrement = 1f,
                                            minimumMajorTickSpacing = 10.dp,
                                            minorTickCount = 0,
                                        ),
                                    yAxisModel =
                                        FloatLinearAxisModel(
                                            0f..yMax.toFloat(),
                                            minimumMajorTickIncrement = 1f,
                                            minorTickCount = 0,
                                        ),
                                    modifier = Modifier.padding(10.dp).height(400.dp),
                                    xAxisLabels = {
                                        try {
                                            if (it != (minX - 1).toFloat() && it != (maxX + 1).toFloat()) it.toString(0) else ""
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
                                                shape = RoundedCornerShape(8.dp),
                                            ) {
                                                Surface(
                                                    shadowElevation = 2.dp,
                                                    shape = MaterialTheme.shapes.small,
                                                    color = colorScheme.surfaceContainerHighest,
                                                ) {
                                                    Box(Modifier.padding(5.dp)) {
                                                        Text(completeGrades[index].second.toString())
                                                    }
                                                }
                                            }
                                        },
                                    )
                                }
                            } else {
                                Text(
                                    "Noch keine Noten vorhanden",
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                } else {
                    // Pre-generated by AI
                    EnhancedAnimatedContent(gradesViewModel.isLoading) { targetState ->
                        Box(Modifier.fillMaxWidth().sizeIn(minHeight = 300.dp)) {
                            if (targetState) {
                                ContainedLoadingIndicator(
                                    Modifier.align(Alignment.Center).padding(
                                        top = gradesViewModel.titleHeight,
                                        bottom = gradesViewModel.closeBarHeight,
                                    ),
                                )
                            } else {
                                val gradeCollections = viewModel.gradeCollections
                                val years = viewModel.years
                                val processedData =
                                    remember(gradeCollections, years, gradesViewModel.filterSubjects, gradesViewModel.deselectedSubjects.size) {
                                        val allGradesByYear =
                                            allFilteredGrades
                                                .filter {
                                                    !gradesViewModel.filterSubjects || !gradesViewModel.deselectedSubjects.contains(it.subject?.name)
                                                }.flatMap { gc -> gc.grades!!.map { gc.interval!!.yearId to normalizeGrade(it.value) } }
                                                .filter { it.second != "N/A" }
                                                .toList()

                                        val groupedByYear =
                                            allGradesByYear
                                                .groupBy({ it.first }, { it.second })
                                                .mapValues { it.value.groupingBy { grade -> grade }.eachCount() }

                                        val sortedYears = groupedByYear.keys.sorted()
                                        val uniqueGrades =
                                            groupedByYear.values
                                                .flatMap { it.keys }
                                                .distinct()
                                                .sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }

                                        val barChartEntries =
                                            sortedYears.map { yearId ->
                                                val yearName =
                                                    years
                                                        .firstOrNull { it.id == yearId }
                                                        ?.name
                                                        .orEmpty()
                                                        .removeRange(0, 2)
                                                        .removeRange(3, 5)
                                                val counts = groupedByYear[yearId] ?: emptyMap()
                                                object : VerticalBarPlotGroupedPointEntry<String, Float> {
                                                    override val x = yearName.ifEmpty { yearId.toString() }
                                                    override val y =
                                                        uniqueGrades.map { grade ->
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

                                        val stackedEntries =
                                            sortedGrades.map { grade ->
                                                object : VerticalBarPlotStackedPointEntry<String, Float> {
                                                    override val x = grade
                                                    override val yOrigin = 0f
                                                    override val y =
                                                        pivotedData[grade]
                                                            ?.let { counts ->
                                                                sortedYears
                                                                    .map { counts[it]?.toFloat() ?: 0f }
                                                                    .scan(0f) { acc, v -> acc + v }
                                                                    .drop(1)
                                                            }
                                                            ?: emptyList()
                                                }
                                            }

                                        val averageGrades =
                                            groupedByYear
                                                .mapNotNull { (yearId, counts) ->
                                                    val total = counts.values.sum()
                                                    if (total > 0) {
                                                        val sum = counts.entries.sumOf { (g, c) -> (g.toIntOrNull() ?: 0) * c }
                                                        yearId to (sum.toFloat() / total)
                                                    } else {
                                                        null
                                                    }
                                                }.toMap()

                                        val avgPlot =
                                            averageGrades.keys.sorted().map { yearId ->
                                                val yearName =
                                                    years
                                                        .firstOrNull { it.id == yearId }
                                                        ?.name
                                                        .orEmpty()
                                                        .removeRange(0, 2)
                                                        .removeRange(3, 5)
                                                DefaultPoint(yearName.ifEmpty { yearId.toString() }, averageGrades[yearId]!!)
                                            }

                                        val polarData =
                                            uniqueGrades.map { grade ->
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
                                            "yearColors" to yearColors,
                                        )
                                    }

                                @Suppress("UNCHECKED_CAST")
                                LazyColumn(
                                    modifier = Modifier.hazeSource(hazeState),
                                    state = secondLazyListState,
                                    contentPadding = PaddingValues(top = gradesViewModel.titleHeight, bottom = gradesViewModel.closeBarHeight),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    item {
                                        val grouped = processedData["grouped"] as Map<Int, Map<String, Int>>
                                        val uniqueGrades = processedData["uniqueGrades"] as List<String>
                                        val barEntries = processedData["barEntries"] as List<VerticalBarPlotGroupedPointEntry<String, Float>>
                                        val maxCount = grouped.values.maxOfOrNull { it.values.maxOrNull() ?: 0 }?.toFloat() ?: 1f

                                        val gradeColors =
                                            listOf(
                                                Color(0xFF4CAF50),
                                                Color(0xFF8BC34A),
                                                Color(0xFFCDDC39),
                                                Color(0xFFFFEB3B),
                                                Color(0xFFFF9800),
                                                Color(0xFFF44336),
                                            )

                                        ChartLayout(
                                            modifier = Modifier.padding(10.dp).height(400.dp),
                                            legend = {
                                                FlowLegend(
                                                    itemCount = uniqueGrades.size,
                                                    symbol = { i ->
                                                        Symbol(
                                                            modifier = Modifier.size(12.dp).clip(CircleShape),
                                                            fillBrush =
                                                                SolidColor(
                                                                    gradeColors[
                                                                        i %
                                                                            gradeColors.size,
                                                                    ],
                                                                ),
                                                        )
                                                    },
                                                    label = { i -> Text("Note ${uniqueGrades[i]}") },
                                                    modifier = Modifier.padding(top = 16.dp),
                                                )
                                            },
                                            legendLocation = LegendLocation.BOTTOM,
                                        ) {
                                            XYGraph(
                                                xAxisModel = CategoryAxisModel(categories = barEntries.map { it.x }),
                                                yAxisModel =
                                                    FloatLinearAxisModel(
                                                        range = 0f..maxCount,
                                                        minimumMajorTickIncrement = 1f,
                                                        minorTickCount = 0,
                                                    ),
                                                xAxisLabels = { it },
                                                yAxisLabels = { it.toInt().toString() },
                                                xAxisTitle = "Jahr",
                                                yAxisTitle = "Anzahl",
                                            ) {
                                                GroupedVerticalBarPlot(
                                                    data = barEntries,
                                                    bar = { _, groupIndex, _ ->
                                                        DefaultVerticalBar(
                                                            brush = SolidColor(gradeColors[groupIndex % gradeColors.size]),
                                                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                                                        )
                                                    },
                                                    animationSpec = KoalaPlotTheme.animationSpec,
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
                                                            fillBrush = SolidColor(yearColors[i]),
                                                        )
                                                    },
                                                    label = { i -> Text(years.firstOrNull { it.id == sortedYears[i] }?.name.orEmpty()) },
                                                    modifier = Modifier.padding(top = 16.dp),
                                                )
                                            },
                                            legendLocation = LegendLocation.BOTTOM,
                                        ) {
                                            XYGraph(
                                                xAxisModel = CategoryAxisModel(categories = sortedGrades),
                                                yAxisModel = FloatLinearAxisModel(range = 0f..maxY, minimumMajorTickIncrement = 5f),
                                                xAxisLabels = { it },
                                                yAxisLabels = { it.toInt().toString() },
                                                xAxisTitle = "Note",
                                                yAxisTitle = "Anzahl",
                                            ) {
                                                StackedVerticalBarPlot(
                                                    data = stacked,
                                                    bar = { _, barIndex ->
                                                        DefaultVerticalBar(
                                                            brush = SolidColor(yearColors[barIndex % yearColors.size]),
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                    },
                                                )
                                            }
                                        }
                                    }
                                    item { Spacer(Modifier.height(20.dp)) }
                                    item {
                                        val avgPlot = processedData["avgPlot"] as List<Point<String, Float>>
                                        if (avgPlot.isEmpty()) {
                                            Text(
                                                "Nicht genügend Daten für die Durchschnittsanzeige vorhanden",
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                textAlign = TextAlign.Center,
                                            )
                                        } else {
                                            ChartLayout(modifier = Modifier.padding(10.dp).fillMaxWidth().height(400.dp)) {
                                                XYGraph(
                                                    xAxisModel = CategoryAxisModel(categories = avgPlot.map { it.x }),
                                                    yAxisModel = FloatLinearAxisModel(range = 0.5f..6.5f),
                                                    xAxisLabels = { it },
                                                    yAxisLabels = { it.toString(0) },
                                                    xAxisTitle = "Jahr",
                                                    yAxisTitle = "Durchschnittsnote",
                                                ) {
                                                    AreaPlot(
                                                        data = avgPlot,
                                                        lineStyle = LineStyle(brush = SolidColor(colorScheme.primary), strokeWidth = 3.dp),
                                                        areaStyle =
                                                            AreaStyle(
                                                                brush = SolidColor(colorScheme.primary.copy(alpha = 0.5f)),
                                                                alpha = 0.5f,
                                                            ),
                                                        areaBaseline = AreaBaseline.ConstantLine(0.5f),
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
                                            val tick =
                                                when {
                                                    radialMax <= 20f -> 5
                                                    radialMax <= 50f -> 10
                                                    radialMax <= 100f -> 20
                                                    else -> 25
                                                }
                                            val radialMaxRounded = ceil(radialMax / tick) * tick
                                            val ticks = (0..radialMaxRounded.toInt() step tick).map { it.toFloat() }

                                            val gradeColors =
                                                listOf(
                                                    Color(0xFF4CAF50),
                                                    Color(0xFF8BC34A),
                                                    Color(0xFFCDDC39),
                                                    Color(0xFFFFEB3B),
                                                    Color(0xFFFF9800),
                                                    Color(0xFFF44336),
                                                )

                                            ChartLayout(
                                                modifier = Modifier.padding(10.dp).fillMaxWidth().aspectRatio(1f),
                                                legend = {
                                                    FlowLegend(
                                                        itemCount = uniqueGrades.size,
                                                        symbol = { i ->
                                                            Symbol(
                                                                modifier = Modifier.size(12.dp).clip(CircleShape),
                                                                fillBrush = SolidColor(gradeColors[i]),
                                                            )
                                                        },
                                                        label = { Text("Note ${uniqueGrades[it]}") },
                                                    )
                                                },
                                                legendLocation = LegendLocation.BOTTOM,
                                            ) {
                                                PolarGraph(
                                                    radialAxisModel = rememberFloatRadialAxisModel(tickValues = ticks),
                                                    angularAxisModel =
                                                        rememberCategoryAngularAxisModel(
                                                            categories =
                                                                sortedYears.map {
                                                                    years
                                                                        .firstOrNull { y ->
                                                                            y.id ==
                                                                                it
                                                                        }?.name
                                                                        .orEmpty()
                                                                },
                                                        ),
                                                    radialAxisLabels = { Text(it.toInt().toString()) },
                                                    angularAxisLabels = { Text(it) },
                                                ) {
                                                    polarData.forEachIndexed { index, series ->
                                                        PolarPlotSeries(
                                                            data = series,
                                                            lineStyle = LineStyle(SolidColor(gradeColors[index]), strokeWidth = 2.dp),
                                                            areaStyle = AreaStyle(SolidColor(gradeColors[index]), alpha = 0.3f),
                                                            symbols = { Symbol(shape = CircleShape, fillBrush = SolidColor(gradeColors[index])) },
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
        }

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = gradesViewModel.closeBarHeight)
                .align(Alignment.TopCenter)
                .enhancedHazeEffect(hazeState, colorScheme.surfaceContainerHighest) {
                    if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) blurEnabled = false
                    progressive = HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                }.onGloballyPositioned {
                    gradesViewModel.titleHeight = with(density) { it.size.height.toDp() }
                },
        ) {
            Text(
                text = "Noten analysieren/vergleichen",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally),
                color = colorScheme.onSurface,
                style = typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EnhancedCheckbox(
                    checked = gradesViewModel.analyzeYears,
                    onCheckedChange = {
                        scope.launch {
                            gradesViewModel.analyzeYears = it
                            if (it) {
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
                        }
                    },
                    enabled = !gradesViewModel.isLoading && viewModel.years.size > 1,
                )
                Text(
                    text = "Jahre analysieren/vergleichen",
                    style = typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EnhancedCheckbox(
                    checked = gradesViewModel.filterSubjects,
                    onCheckedChange = { gradesViewModel.filterSubjects = it },
                    enabled = !gradesViewModel.isLoading && viewModel.years.size > 1,
                )
                Text(
                    text = "Fächer filtern",
                    modifier = Modifier.weight(1f),
                    style = typography.bodyLarge,
                )
                EnhancedAnimatedVisibility(gradesViewModel.filterSubjects) {
                    EnhancedIconButton(
                        onClick = {
                            gradesViewModel.filterShown = !gradesViewModel.filterShown
                        },
                    ) {
                        EnhancedAnimatedContent(gradesViewModel.filterShown) {
                            if (it) {
                                Icon(Icons.Outlined.Remove, null)
                            } else {
                                Icon(Icons.Outlined.Add, null)
                            }
                        }
                    }
                }
            }

            EnhancedAnimatedVisibility(gradesViewModel.filterSubjects && gradesViewModel.filterShown) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    (if (gradesViewModel.analyzeYears) allFilteredGrades.toList() else filteredGrades)
                        .map { it.subject?.name ?: "" }
                        .toSet()
                        .forEach { subject ->
                            FilterChip(
                                selected = !gradesViewModel.deselectedSubjects.contains(subject),
                                onClick = {
                                    if (gradesViewModel.deselectedSubjects.contains(subject)) {
                                        gradesViewModel.deselectedSubjects.remove(subject)
                                    } else {
                                        gradesViewModel.deselectedSubjects.add(subject)
                                    }
                                },
                                label = { Text(subject) },
                            )
                        }
                }
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .enhancedHazeEffect(hazeState, colorScheme.surfaceContainerHighest) {
                    if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) blurEnabled = false
                    progressive = HazeProgressive.verticalGradient()
                }.onGloballyPositioned {
                    gradesViewModel.closeBarHeight = with(density) { it.size.height.toDp() }
                },
        ) {
            EnhancedButton(
                onClick = {
                    scope.launch {
                        gradesViewModel.toolbarState = 0
                        gradesViewModel.contentBlurred = false
                        delay(250)
                        if (gradesViewModel.toolbarState == 0) gradesViewModel.userScrollEnabled = true
                    }
                },
                modifier = Modifier.padding(10.dp).align(Alignment.CenterEnd),
            ) {
                Text("Schließen")
            }
        }
    }
}
