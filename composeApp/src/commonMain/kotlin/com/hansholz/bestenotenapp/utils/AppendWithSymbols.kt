package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.hansholz.bestenotenapp.theme.FontFamilies

@Composable
fun AnnotatedString.Builder.appendWithSymbols(
    text: String,
    symbolFont: FontFamily = FontFamilies.Symbols(),
    symbolRegex: Regex = Regex("[➞➔➜➡➤▶↳]"),
) {
    val startOffset = this.length
    append(text)
    symbolRegex.findAll(text).forEach { result ->
        addStyle(
            style = SpanStyle(fontFamily = symbolFont),
            start = startOffset + result.range.first,
            end = startOffset + result.range.last + 1,
        )
    }
}
