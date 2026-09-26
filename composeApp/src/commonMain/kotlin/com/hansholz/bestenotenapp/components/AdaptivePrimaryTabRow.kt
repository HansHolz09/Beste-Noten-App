package com.hansholz.bestenotenapp.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNativePrimaryTabRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptivePrimaryTabRow(
    labels: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    divider: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val nativeTabRow = LocalNativePrimaryTabRow.current
    if (nativeComponentsEnabled && nativeTabRow != null) {
        nativeTabRow(labels, selectedTabIndex, onTabSelected, modifier)
    } else {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = modifier,
            containerColor = containerColor,
            divider = divider,
            tabs = content,
        )
    }
}
