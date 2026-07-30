package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
    reduceTopPadding: Boolean = false,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            modifier.padding(
                top = if (reduceTopPadding) 8.dp else 24.dp,
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp,
            ),
    )
}
