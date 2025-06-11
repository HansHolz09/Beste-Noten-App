@file:Suppress("DEPRECATION")

package com.hansholz.bestenotenapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hansholz.bestenotenapp.components.EnhancedAnimated
import com.hansholz.bestenotenapp.components.enhancedHazeEffect
import com.hansholz.bestenotenapp.main.LocalShowCollectionsWithoutGrades
import com.hansholz.bestenotenapp.main.LocalShowGradeHistory
import com.hansholz.bestenotenapp.main.LocalShowTeachersWithFirstname
import com.hansholz.bestenotenapp.main.ViewModel
import com.nomanr.animate.compose.presets.specials.JackInTheBox
import com.nomanr.animate.compose.presets.zoomingextrances.ZoomIn
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Grades(
    viewModel: ViewModel
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val windowWithSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    val showGradeHistory by LocalShowGradeHistory.current
    val showCollectionsWithoutGrades by LocalShowCollectionsWithoutGrades.current
    val showTeachersWithFirstname by LocalShowTeachersWithFirstname.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            if (viewModel.collections.isEmpty()) {
                viewModel.getCollections()
            }
            if (viewModel.years.isEmpty()) {
                viewModel.years.addAll(viewModel.api.getYears().data)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    EnhancedAnimated(
                        preset = JackInTheBox(),
                    ) {
                        Text("Noten", fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier.clip(FloatingActionButtonDefaults.shape).enhancedHazeEffect(viewModel.hazeBackgroundState, colorScheme.primaryContainer),
                containerColor = Color.Transparent,
                contentColor = colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Icon(Icons.Outlined.BarChart, null)
            }
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            var topPadding by remember { mutableStateOf(0.dp) }
            val contentPadding = PaddingValues(top = topPadding, bottom = innerPadding.calculateBottomPadding())

            val pagerState = rememberPagerState { 2 }
            HorizontalPager(pagerState, Modifier.hazeSource(viewModel.hazeBackgroundState)) {
                when(it) {
                    0 -> {
                        LazyColumn(contentPadding = contentPadding) {
                            items(
                                viewModel
                                    .collections
                                    .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
                                    .sortedByDescending { it.givenAt }
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
                                                Text("${it.type} vom ${LocalDate.parse(it.givenAt).let { "${it.dayOfMonth}.${it.monthNumber}.${it.year}" }}")

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
                            modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
                            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
                        ) {
                            viewModel.collections
                                .filter { if (showCollectionsWithoutGrades) true else it.grades?.size != 0 }
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
                                                        Text("Gegeben am ${LocalDate.parse(it.givenAt).let { "${it.dayOfMonth}.${it.monthNumber}.${it.year}" }}")

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
        }
    )
//    LazyColumn {
//        items(viewModel.years) {
//            Button(
//                onClick = {
//                    scope.launch {
//                        viewModel.api.setCurrentYear(it.id)
//                        viewModel.getCollections()
//                    }
//                }
//            ) {
//                Text("${it.name} (${it.from} - ${it.to}")
//            }
//        }
//
//        item {
//            Button(
//                onClick = {
//                    scope.launch {
//                        viewModel.api.setCurrentYear()
//                        viewModel.getCollections()
//                    }
//                }
//            ) {
//                Text("Aktuelles Jahr")
//            }
//        }
//
//        item {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Checkbox(
//                    checked = viewModel.showCollectionsWithoutGrades,
//                    onCheckedChange = { viewModel.showCollectionsWithoutGrades = it },
//                )
//                Spacer(Modifier.width(10.dp))
//                Text("Leistungen ohne Noten anzeigen")
//            }
//        }
//    }
}