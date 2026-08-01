package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.Cookie6Sided
import androidx.compose.material3.MaterialShapes.Companion.Gem
import androidx.compose.material3.MaterialShapes.Companion.Ghostish
import androidx.compose.material3.MaterialShapes.Companion.Pill
import androidx.compose.material3.MaterialShapes.Companion.PixelCircle
import androidx.compose.material3.MaterialShapes.Companion.Slanted
import androidx.compose.material3.MaterialShapes.Companion.SoftBurst
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Block
import com.hansholz.bestenotenapp.api.models.Level
import com.hansholz.bestenotenapp.utils.GradeScale
import com.hansholz.bestenotenapp.utils.gradeScale
import com.hansholz.bestenotenapp.utils.parseGradeValue
import org.kodein.emoji.compose.m3.TextWithNotoImageEmoji
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GradeValueBox(
    gradeValue: String?,
    level: Level? = null,
) {
    val value = parseGradeValue(gradeValue)
    val scale = level?.gradeScale() ?: value?.let { if (it > 6f) GradeScale(15, 0) else GradeScale(1, 6) }
    val quality = value?.takeIf { scale?.contains(it) == true }?.let { scale!!.quality(it) }
    val color =
        quality?.let {
            if (it < 0.5f) {
                lerp(Color(0xFFF44336), Color(0xFFFFEB3B), it * 2f)
            } else {
                lerp(Color(0xFFFFEB3B), Color(0xFF4CAF50), (it - 0.5f) * 2f)
            }
        } ?: colorScheme.errorContainer
    val shapes = listOf(SoftBurst, Pill, Ghostish, Slanted, Gem, Cookie6Sided)
    val shape =
        quality
            ?.let { shapes[((1f - it) * shapes.lastIndex).roundToInt()] }
            ?.toShape()
            ?: PixelCircle.toShape()
    Box(Modifier.clip(shape).background(color).size(30.dp)) {
        gradeValue?.let {
            TextWithNotoImageEmoji(
                text = it,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black,
                fontSize = 20.sp,
            )
        } ?: Icon(
            imageVector = MaterialSymbols.Rounded.Block,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(3.dp),
            tint = colorScheme.onErrorContainer,
        )
    }
}
