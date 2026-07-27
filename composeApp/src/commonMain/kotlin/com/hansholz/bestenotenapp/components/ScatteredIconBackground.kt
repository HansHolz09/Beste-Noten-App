package com.hansholz.bestenotenapp.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hansholz.bestenotenapp.theme.FontFamilies
import com.hansholz.bestenotenapp.theme.LocalAnimationsEnabled
import com.hansholz.bestenotenapp.theme.LocalUseCustomColorScheme
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

sealed class ScatterItem {
    data class IconItem(
        val icon: ImageVector,
    ) : ScatterItem()

    data class TextItem(
        val text: String,
    ) : ScatterItem()
}

data class ScatterConfig(
    val cellSize: Dp = 75.dp,
    val itemSizeFraction: Float = 0.6f,
    val jitterFraction: Float = 0.20f,
    val maxRotationDegrees: Float = 18f,
    val minScale: Float = 0.9f,
    val maxScale: Float = 1.1f,
    val sizeBucketPx: Float = 6f,
)

private data class ScatterSlot(
    val itemIndex: Int,
    val colorIndex: Int,
    val jitterXFraction: Float,
    val jitterYFraction: Float,
    val rotation: Float,
    val scale: Float,
)

private fun splitMix64(seed: Long): Long {
    var z = seed + 0x9E3779B97F4A7C15UL.toLong()
    z = (z xor (z ushr 30)) * 0xBF58476D1CE4E5B9UL.toLong()
    z = (z xor (z ushr 27)) * 0x94D049BB133111EBUL.toLong()
    return z xor (z ushr 31)
}

private fun cellSeed(
    baseSeed: Long,
    col: Int,
    row: Int,
): Long {
    val packed = (col.toLong() shl 32) xor (row.toLong() and 0xFFFFFFFFL)
    return splitMix64(baseSeed xor splitMix64(packed))
}

@Composable
private fun colorStates(): List<State<Color>> {
    val cs = MaterialTheme.colorScheme
    val useCustom = LocalUseCustomColorScheme.current.value
    val animationsEnabled = LocalAnimationsEnabled.current.value
    return listOf(
        cs.primary to Color(0xFFFF5C7A),
        cs.secondary to Color(0xFFFAA900),
        cs.tertiary to Color(0xFFFFC94D),
        cs.onPrimaryContainer to Color(0xFFFCDC84),
        cs.onSecondaryContainer to Color(0xFFA2CE4F),
        cs.onTertiaryContainer to Color(0xFF4DD9B5),
        cs.outline to Color(0xFF9A8CFF),
        cs.outlineVariant to Color(0xFF4DB8FF),
    ).map { (custom, fixed) ->
        animateColorAsState(
            targetValue = if (useCustom) custom else fixed,
            animationSpec = tween(if (animationsEnabled) 750 else 0),
        )
    }
}

@Composable
fun Modifier.scatteredIconBackground(
    items: List<ScatterItem>,
    colors: List<State<Color>> = colorStates(),
    config: ScatterConfig = ScatterConfig(),
    alpha: Float = 1f,
): Modifier {
    require(items.isNotEmpty()) { "items darf nicht leer sein" }
    require(colors.isNotEmpty()) { "colors darf nicht leer sein" }

    val fontFamily = FontFamilies.Sniglet
    val seed = rememberSaveable { Random.nextLong(1000L) }

    val painters: List<Painter?> =
        items.map { item ->
            when (item) {
                is ScatterItem.IconItem -> rememberVectorPainter(item.icon)
                is ScatterItem.TextItem -> null
            }
        }
    val textMeasurer = rememberTextMeasurer()

    val slotCache = remember(items.size, colors.size) { HashMap<Long, ScatterSlot>() }
    val textLayoutCache = remember(items, fontFamily) { HashMap<Long, TextLayoutResult>() }
    val iconMaskCache = remember(items) { HashMap<Long, ImageBitmap>() }
    val canvasDrawScope = remember { CanvasDrawScope() }

    fun slotFor(
        col: Int,
        row: Int,
    ): ScatterSlot {
        val key = (col.toLong() shl 32) xor (row.toLong() and 0xFFFFFFFFL)
        return slotCache.getOrPut(key) {
            val rnd = Random(cellSeed(seed, col, row))
            ScatterSlot(
                itemIndex = rnd.nextInt(items.size),
                colorIndex = rnd.nextInt(colors.size),
                jitterXFraction = (rnd.nextFloat() - 0.5f) * 2f * config.jitterFraction,
                jitterYFraction = (rnd.nextFloat() - 0.5f) * 2f * config.jitterFraction,
                rotation = (rnd.nextFloat() - 0.5f) * 2f * config.maxRotationDegrees,
                scale = config.minScale + rnd.nextFloat() * (config.maxScale - config.minScale),
            )
        }
    }

    fun bucket(sizePx: Float): Int = (sizePx / config.sizeBucketPx).roundToInt().coerceAtLeast(1)

    fun textLayoutFor(
        itemIndex: Int,
        text: String,
        sizePx: Float,
        density: Float,
    ): TextLayoutResult {
        val sizeBucket = bucket(sizePx)
        val key = (itemIndex.toLong() shl 32) xor sizeBucket.toLong()
        return textLayoutCache.getOrPut(key) {
            val bucketedSizePx = sizeBucket * config.sizeBucketPx
            textMeasurer.measure(
                text,
                TextStyle(
                    color = Color.Black,
                    fontSize = (bucketedSizePx / density).sp,
                    fontFamily = fontFamily,
                ),
            )
        }
    }

    fun iconMaskFor(
        itemIndex: Int,
        painter: Painter,
        sizePx: Float,
        density: Density,
    ): ImageBitmap {
        val sizeBucket = bucket(sizePx)
        val key = (itemIndex.toLong() shl 32) xor sizeBucket.toLong()
        return iconMaskCache.getOrPut(key) {
            val bucketedSizePx = (sizeBucket * config.sizeBucketPx).roundToInt().coerceAtLeast(1)
            val bitmap = ImageBitmap(bucketedSizePx, bucketedSizePx)
            val canvas = Canvas(bitmap)
            canvasDrawScope.draw(
                density = density,
                layoutDirection = LayoutDirection.Ltr,
                canvas = canvas,
                size = Size(bucketedSizePx.toFloat(), bucketedSizePx.toFloat()),
            ) {
                with(painter) {
                    draw(size = size)
                }
            }
            bitmap
        }
    }

    return this.then(
        Modifier.drawWithCache {
            val cellPx = config.cellSize.toPx()
            val itemSizePxBase = cellPx * config.itemSizeFraction

            onDrawBehind {
                val colStart = floor(-cellPx / cellPx).toInt() - 1
                val rowStart = floor(-cellPx / cellPx).toInt() - 1
                val colEnd = kotlin.math.ceil(size.width / cellPx).toInt() + 1
                val rowEnd = kotlin.math.ceil(size.height / cellPx).toInt() + 1

                for (row in rowStart..rowEnd) {
                    for (col in colStart..colEnd) {
                        val slot = slotFor(col, row)
                        val itemSizePx = itemSizePxBase * slot.scale
                        val centerX = col * cellPx + cellPx / 2f + slot.jitterXFraction * cellPx
                        val centerY = row * cellPx + cellPx / 2f + slot.jitterYFraction * cellPx
                        val color = colors[slot.colorIndex].value
                        val item = items[slot.itemIndex]

                        translate(left = centerX, top = centerY) {
                            rotate(degrees = slot.rotation, pivot = androidx.compose.ui.geometry.Offset.Zero) {
                                when (item) {
                                    is ScatterItem.IconItem -> {
                                        val painter = painters[slot.itemIndex] ?: return@rotate
                                        val mask =
                                            iconMaskFor(
                                                itemIndex = slot.itemIndex,
                                                painter = painter,
                                                sizePx = itemSizePx,
                                                density = this@onDrawBehind,
                                            )
                                        translate(left = -itemSizePx / 2f, top = -itemSizePx / 2f) {
                                            drawImage(
                                                image = mask,
                                                dstSize =
                                                    IntSize(
                                                        itemSizePx.roundToInt(),
                                                        itemSizePx.roundToInt(),
                                                    ),
                                                alpha = alpha,
                                                colorFilter = ColorFilter.tint(color),
                                            )
                                        }
                                    }

                                    is ScatterItem.TextItem -> {
                                        val layout =
                                            textLayoutFor(
                                                itemIndex = slot.itemIndex,
                                                text = item.text,
                                                sizePx = itemSizePx,
                                                density = density,
                                            )
                                        translate(
                                            left = -layout.size.width / 2f,
                                            top = -layout.size.height / 2f,
                                        ) {
                                            drawText(
                                                textLayoutResult = layout,
                                                color = color,
                                                alpha = alpha,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun ScatteredIconBackground(
    items: List<ScatterItem>,
    modifier: Modifier = Modifier,
    colors: List<State<Color>> = colorStates(),
    config: ScatterConfig = ScatterConfig(),
    alpha: Float = 1f,
) {
    Box(
        modifier.fillMaxSize().scatteredIconBackground(
            items = items,
            colors = colors,
            config = config,
            alpha = alpha,
        ),
    )
}
