package com.hansholz.bestenotenapp.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.hansholz.bestenotenapp.NativeComponentBridge
import kotlin.random.Random

@Composable
internal fun NativePrimaryTabRow(
    bridge: NativeComponentBridge,
    labels: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier,
) {
    val owner = remember { "primary-tabs-${Random.nextLong()}" }

    SideEffect {
        bridge.showPrimaryTabs(owner, labels, selectedTabIndex, onTabSelected)
    }
    DisposableEffect(owner) {
        onDispose { bridge.hidePrimaryTabs(owner) }
    }

    Spacer(modifier)
}
