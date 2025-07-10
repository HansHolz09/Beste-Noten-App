package com.hansholz.bestenotenapp.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import bestenotenapp.composeapp.generated.resources.KeaniaOne_Regular
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.Schoolbell_Regular
import bestenotenapp.composeapp.generated.resources.Sniglet_Regular
import org.jetbrains.compose.resources.Font

object FontFamilies {
    @Composable
    fun Sniglet() = FontFamily(Font(Res.font.Sniglet_Regular))
    @Composable
    fun KeaniaOne() = FontFamily(Font(Res.font.KeaniaOne_Regular))
    @Composable
    fun Schoolbell() = FontFamily(Font(Res.font.Schoolbell_Regular, weight = FontWeight.Normal))

    @Composable
    fun allFontResources() = listOf(
        Res.font.Sniglet_Regular,
        Res.font.KeaniaOne_Regular,
        Res.font.Schoolbell_Regular,
    )
}

@Composable
fun AppTypography() = Typography().run {
    val fontFamily = FontFamilies.Sniglet()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}