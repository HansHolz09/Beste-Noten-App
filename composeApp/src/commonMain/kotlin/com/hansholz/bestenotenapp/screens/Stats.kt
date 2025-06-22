package com.hansholz.bestenotenapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.koalaplot.core.pie.PieSliceScope
import io.github.koalaplot.core.util.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun Stats() {
//    val api = remember {
//        BesteSchuleApi("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxMiIsImp0aSI6IjQ1ZjAyMzc3ODY4Zjc4ZWVmNmUzMmNhYzUxNmE3Y2IwZDRiY2M2Njg5ZmE4ZjNkN2I3MTU5OTdlNDQ0MjFlYmI5OWFkYjE2ZjRjMjY2NGM0IiwiaWF0IjoxNzQ5MjMwOTE1Ljk2ODYyNCwibmJmIjoxNzQ5MjMwOTE1Ljk2ODYyNSwiZXhwIjoxNzgwNzY2OTE1Ljk2NjgxMywic3ViIjoiMjI0OSIsInNjb3BlcyI6W119.UMQF6Nu0i_yE053ywq_i-9dt29zKDpTbLLLDoWEUbDpf2iYGTZRDCuwJvK-zg-gQCmF7cd2honCneRBZ9hzsxHzRE5p1qrB220DYMdEJXdhjgilULrXUW-FbUVQ4QwYWeFvQellJB2SAqxvBPnJsRNYgTFQNvo-5ZTg4Rgi9nhKVQ1nkReWfFmHYGWBIFmwhvZ4572m_9wQHzr2ELf8tXr1X2TaVGuz3A_ng_jMqiXgVtzDhC3eA55rsKsZpDupLsrF58RQ7j_GmVo_D5U7bzXbL_I4A4ZP6mjGEiwrDWISPfNoicaH3DIT8UQMMd0qoiPJ1iQS89YE4i-o6EwncPN6TWdOuETRbeFVJW3UBtXbJQNpQf0-KEcaCTX-tn36ofRCILdueu1AgsoZWkjVzYvAc5zKIaEvc3eFFu68XLLGoUvxm08OtomP_JDprzf2ZEAyzBQ-a2_ycNYMHbQCivzTKeVHhyNqqYP67bNfpzGcUt3pNqTEDlGT3v9aBCJKz-T6z1XRKZHVl5hsh9t3k8MsyTu-aHfEDOL5hPjYqxuhdgqr0ZZPq5yY6jawPih9slJx7SbT7PJUy4iNJUEVZBgt2SIRIvejsLaUehtJ1_aeQ4Orumo5AoPyr6vKChi-naUPOs0G_ySg2FAt4qgk7W7qBHuY-7EmavO9hc8hjsf4")
//    }
//
//    val lessonStudentStats = remember { mutableStateOf<LessonStudentStats?>(null) }
//    val dayStudentStats = remember { mutableStateOf<DayStudentStats?>(null) }
//    val lessonStudentDetail = remember { mutableStateListOf<LessonStudentDetail>() }
//
//    var rooms by remember { mutableStateOf(listOf<Pair<String, Int>>()) }
//
//    LaunchedEffect(Unit) {
//        dayStudentStats.value = api.getDayStudentStats().data[0]
//        lessonStudentStats.value = api.getLessonStudentStats().data[0]
//        api.getYears().data.last {
//            lessonStudentDetail.addAll(api.getLessonStudentDetails(it.from, it.to).data)
//        }
//        rooms = lessonStudentDetail
//            .map { it.lesson.rooms[0].localId }
//            .groupingBy { it }
//            .eachCount()
//            .toList()
//            .take(10)
//            .sortedByDescending { it.second }
//            .map { value -> (value.first ?: "") to value.second }
//    }
//
//    LazyColumn {
//        item {
//            Text("Schultage: ${dayStudentStats.value?.count}")
//        }
//        item {
//            Text("Fehltage: ${dayStudentStats.value?.notPresentCount}")
//        }
//        item {
//            Text("Entschuldigte Fehltage: ${dayStudentStats.value?.notPresentWithAbsenceCount}")
//        }
//        item {
//            Text("Unentschuldigte Fehltage: ${dayStudentStats.value?.notPresentWithoutAbsenceCount}")
//        }
//        item {
//            Text("Schulstunden: ${dayStudentStats.value?.lessonsCount}")
//        }
//        item {
//            Text("Entschuldigte Fehlstunden: ${dayStudentStats.value?.lessonsNotPresentWithAbsenceCount}")
//        }
//        item {
//            Text("Unentschuldigte Fehlstunden: ${dayStudentStats.value?.lessonsNotPresentWithoutAbsenceCount}")
//        }
//        item {
//            Text("Schulstunden zu spät: ${lessonStudentStats.value?.tooLateWithAbsenceSum}")
//        }
//        item {
//            Text("Schulstunden zu früh: ${lessonStudentStats.value?.tooEarlyWithAbsenceSum}")
//        }
//        item {
//            Text("Schulzeug vergessen: ${lessonStudentStats.value?.missingEquipmentSum}")
//        }
//        item {
//            Text("Hausaufgaben vergessen: ${lessonStudentStats.value?.missingHomeworkSum}")
//        }
//        item {
//            Text("Schulstunden dieses Schuljahr: ${lessonStudentDetail.size}")
//        }
//        item {
//            AnimatedVisibility(rooms.isNotEmpty()) {
//                Column {
//                    Text("Wie oft in welchem Raum gewesen?")
//                    Spacer(Modifier.height(10.dp))
//                    var text by remember { mutableStateOf("Klicken") }
//                    PieChart(
//                        values = rooms.map { value -> value.second.toFloat() },
//                        slice = {
//                            val colors = remember(rooms.size) { generateHueColorPalette(rooms.size) }
//                            DefaultSlice(
//                                color = colors[it].copy(0.85f),
//                                gap = 5f,
//                                hoverExpandFactor = 1.1f,
//                                hoverElement = {
//                                    Surface(
//                                        shadowElevation = 2.dp,
//                                        shape = MaterialTheme.shapes.small,
//                                        color = colorScheme.surfaceContainerHighest,
//                                    ) {
//                                        Box(Modifier.padding(5.dp)) {
//                                            Text(rooms[it].second.toString())
//                                        }
//                                    }
//                                },
//                                clickable = true,
//                                onClick = {
//                                    text = rooms[it].second.toString() + " mal"
//                                }
//                            )
//                        },
//                        label = {
//                            Text(rooms[it].first)
//                        },
//                        labelConnector = {
//                            BezierLabelConnector(connectorStroke = Stroke(3f))
//                        },
//                        holeSize = 0.5f,
//                        holeContent = {
//                            AnimatedContent(
//                                targetState = text,
//                                modifier = Modifier.fillMaxSize(),
//                            ) {
//                                Box(Modifier.fillMaxSize()) {
//                                    Text(it, Modifier.align(Alignment.Center))
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
}

@ExperimentalKoalaPlotApi
@Composable
fun PieSliceScope.DefaultSlice(
    color: Color,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    hoverExpandFactor: Float = 1.0f,
    hoverElement: @Composable () -> Unit = {},
    clickable: Boolean = false,
    antiAlias: Boolean = false,
    gap: Float = 0.0f,
    cornerRadius: Dp = 5.dp,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val targetOuterRadius by animateFloatAsState(outerRadius * if (isHovered) hoverExpandFactor else 1f)

    // Den Dp-Wert in Pixel umwandeln
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }

    // Die Shape mit dem neuen Parameter erstellen
    val shape = Slice(
        startAngle = pieSliceData.startAngle.toDegrees().value.toFloat() + gap,
        angle = pieSliceData.angle.toDegrees().value.toFloat() - 2 * gap,
        innerRadius = innerRadius,
        outerRadius = targetOuterRadius,
        cornerRadius = cornerRadiusPx
    )

    Box(
        modifier = modifier.fillMaxSize()
            .drawWithContent {
                drawIntoCanvas {
                    val path = (shape.createOutline(size, layoutDirection, this) as Outline.Generic).path
                    it.drawPath(path, Paint().apply { isAntiAlias = antiAlias; this.color = color })
                    if (border != null) {
                        it.withSaveLayer(Rect(Offset.Zero, size), Paint().apply { isAntiAlias = antiAlias }) {
                            val clip = Path().apply {
                                addRect(Rect(Offset.Zero, size))
                                op(this, path, PathOperation.Difference)
                            }
                            it.drawPath(
                                path,
                                Paint().apply {
                                    isAntiAlias = antiAlias
                                    strokeWidth = border.width.toPx()
                                    style = PaintingStyle.Stroke
                                    border.brush.applyTo(size, this, 1f)
                                }
                            )
                            it.drawPath(
                                clip,
                                Paint().apply { isAntiAlias = antiAlias; blendMode = BlendMode.Clear }
                            )
                        }
                    }
                }
                drawContent()
            }.clip(shape)
            .then(
                if (clickable) Modifier.clickable(enabled = true, role = Role.Button, onClick = onClick)
                else Modifier
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .hoverableElement(hoverElement)
            .hoverable(interactionSource)
    ) {}
}

private class Slice(
    private val startAngle: Float,
    private val angle: Float,
    private val innerRadius: Float,
    private val outerRadius: Float,
    private val cornerRadius: Float = 0f // Neuer Konstruktor-Parameter
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Wenn kein Eckradius angegeben ist, wird die originale, scharfkantige Logik verwendet.
        if (cornerRadius <= 0f) {
            return Outline.Generic(createOriginalPath(size))
        }
        return Outline.Generic(createRoundedPath(size))
    }

    // Die originale Methode, um einen scharfkantigen Pfad zu erstellen.
    private fun createOriginalPath(size: Size): Path {
        val radius = size.width / 2f * outerRadius
        val holeRadius = size.width / 2f * innerRadius
        val center = Offset(size.width / 2f, size.height / 2f)

        return Path().apply {
            moveTo(center + polarToCartesian(holeRadius, startAngle.deg))
            lineTo(center + polarToCartesian(radius, startAngle.deg))
            arcTo(Rect(center, radius), startAngle, angle, false)
            lineTo(center + polarToCartesian(holeRadius, (startAngle + angle).deg))
            if (holeRadius > 0f) {
                arcTo(Rect(center, holeRadius), startAngle + angle, -angle, false)
            }
            close()
        }
    }

    // Die neue Methode, die einen Pfad mit abgerundeten Ecken erstellt.
    private fun createRoundedPath(size: Size): Path {
        val radius = size.width / 2f * outerRadius
        val holeRadius = size.width / 2f * innerRadius
        val center = Offset(size.width / 2f, size.height / 2f)
        val endAngle = startAngle + angle

        // Begrenze den Eckradius, um visuelle Fehler zu vermeiden.
        val safeCr = min(cornerRadius, (radius - holeRadius) / 2f)

        // Wenn das Segment zu dünn für die Rundung ist, zeichne es scharfkantig.
        val outerAngleOffset = asin(safeCr / (radius - safeCr)).toDegrees().value.toFloat()
        if (2 * outerAngleOffset > angle) {
            return createOriginalPath(size)
        }
        val innerAngleOffset = if (holeRadius > 0) asin(safeCr / (holeRadius + safeCr)).toDegrees().value.toFloat() else 0f

        return Path().apply {
            // Startpunkt: auf der inneren Kante, aber um den Radius vom Startwinkel versetzt
            val p1 = center + polarToCartesian(holeRadius, (startAngle + innerAngleOffset).toDegrees())
            if (holeRadius > 0) {
                moveTo(p1.x, p1.y)
            } else {
                moveTo(center.x, center.y)
            }

            // Ecke 1: Übergang innere Kante -> radiale Linie (konkav)
            if (holeRadius > 0) {
                val c1Center = center + polarToCartesian(holeRadius + safeCr, startAngle.deg)
                arcTo(Rect(c1Center, safeCr), startAngle + 180, 90f, false)
            }

            // Ecke 2: Übergang radiale Linie -> äußere Kante (konvex)
            val c2Center = center + polarToCartesian(radius - safeCr, startAngle.deg)
            arcTo(Rect(c2Center, safeCr), startAngle - 90, 90f, false)

            // Hauptbogen außen
            arcTo(Rect(center, radius), startAngle + outerAngleOffset, angle - 2 * outerAngleOffset, false)

            // Ecke 3: Übergang äußere Kante -> radiale Linie (konvex)
            val c3Center = center + polarToCartesian(radius - safeCr, endAngle.deg)
            arcTo(Rect(c3Center, safeCr), endAngle, 90f, false)

            // Ecke 4: Übergang radiale Linie -> innere Kante (konkav)
            if (holeRadius > 0) {
                val c4Center = center + polarToCartesian(holeRadius + safeCr, endAngle.deg)
                arcTo(Rect(c4Center, safeCr), endAngle + 90, 90f, false)

                // Hauptbogen innen
                arcTo(Rect(center, holeRadius), endAngle - innerAngleOffset, -(angle - 2 * innerAngleOffset), false)
            }

            close()
        }
    }
}


fun polarToCartesian(radius: Float, angle: Degrees): Offset = polarToCartesian(radius, angle.toRadians())

fun polarToCartesian(radius: Float, angle: Radians): Offset {
    return Offset((radius * cos(angle.value)).toFloat(), (radius * sin(angle.value)).toFloat())
}

fun Path.lineTo(offset: Offset) {
    lineTo(offset.x, offset.y)
}

fun Path.moveTo(offset: Offset) {
    moveTo(offset.x, offset.y)
}
