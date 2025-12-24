package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TwoToneLinearWavyProgressIndicator(
    progress: Float,
    split: Float = 0.5f,
    firstColor: Color = WavyProgressIndicatorDefaults.indicatorColor,
    secondColor: Color = WavyProgressIndicatorDefaults.indicatorColor,
    modifier: Modifier = Modifier,
) {
    val p = progress.coerceIn(0f, 1f)
    val s = split.coerceIn(0f, 1f)

    Box(modifier = modifier.clipToBounds()) {
        LinearWavyProgressIndicator(
            progress = { p },
            color = Color.Transparent,
            trackColor = firstColor.copy(0.5f),
            modifier =
                Modifier
                    .matchParentSize()
                    .drawWithContent outer@{
                        clipRect(left = 0f, top = 0f, right = size.width * s, bottom = size.height) {
                            this@outer.drawContent()
                        }
                    },
        )

        LinearWavyProgressIndicator(
            progress = { p },
            color = Color.Transparent,
            trackColor = secondColor.copy(0.5f),
            modifier =
                Modifier
                    .matchParentSize()
                    .drawWithContent outer@{
                        clipRect(left = size.width * s, top = 0f, right = size.width, bottom = size.height) {
                            this@outer.drawContent()
                        }
                    },
        )

        LinearWavyProgressIndicator(
            progress = { p },
            color = firstColor,
            trackColor = Color.Transparent,
            modifier =
                Modifier
                    .matchParentSize()
                    .drawWithContent outer@{
                        val right = size.width * minOf(p, s)
                        clipRect(left = 0f, top = 0f, right = right + size.width * 0.05f, bottom = size.height) {
                            this@outer.drawContent()
                        }
                    },
        )

        LinearWavyProgressIndicator(
            progress = { p },
            color = secondColor,
            trackColor = Color.Transparent,
            modifier =
                Modifier
                    .matchParentSize()
                    .drawWithContent outer@{
                        val left = size.width * s
                        val right = size.width * p
                        clipRect(left = left, top = 0f, right = right + size.width * 0.05f, bottom = size.height) {
                            this@outer.drawContent()
                        }
                    },
        )
    }
}
