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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Block
import org.kodein.emoji.compose.m3.TextWithNotoImageEmoji

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GradeValueBox(gradeValue: String?) {
    val color =
        when (gradeValue?.take(1)) {
            "1" -> Color(0xFF4CAF50)
            "2" -> Color(0xFF8BC34A)
            "3" -> Color(0xFFCDDC39)
            "4" -> Color(0xFFFFEB3B)
            "5" -> Color(0xFFFF9800)
            "6" -> Color(0xFFF44336)
            else -> colorScheme.errorContainer
        }
    val shape =
        when (gradeValue?.take(1)) {
            "1" -> SoftBurst
            "2" -> Pill
            "3" -> Ghostish
            "4" -> Slanted
            "5" -> Gem
            "6" -> Cookie6Sided
            else -> PixelCircle
        }.toShape()
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
