package com.hansholz.bestenotenapp.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp

internal val LocalNativeComponentsEnabled = compositionLocalOf { mutableStateOf(false) }
internal val LocalNativeSwitch =
    compositionLocalOf<(@Composable (Boolean, (Boolean) -> Unit, Boolean, Modifier, Boolean) -> Unit)?> { null }
internal val LocalNativeAppearanceSelector =
    compositionLocalOf<(@Composable (Int, (Int) -> Unit, Modifier) -> Unit)?> { null }
internal val LocalNativeDialogBackdrop =
    compositionLocalOf<(@Composable (Modifier, List<Rect>) -> Unit)?> { null }
internal val LocalNativePrimaryTabRow =
    compositionLocalOf<(@Composable (List<String>, Int, (Int) -> Unit, Modifier) -> Unit)?> { null }
internal val LocalNativeDatePicker =
    compositionLocalOf<(@Composable (Long?, (Long) -> Unit, Modifier) -> Unit)?> { null }
internal val LocalNativeTimePicker =
    compositionLocalOf<(@Composable (Int, Int, (Int, Int) -> Unit, Modifier) -> Unit)?> { null }
internal val LocalHideNativeDateTimePickers = compositionLocalOf { {} }
internal val LocalGlobalEasterEgg = compositionLocalOf<((String) -> Unit)?> { null }
internal val LocalHideNativeInterop = compositionLocalOf { {} }
internal val LocalNavigationDrawerTopPadding = compositionLocalOf<Dp?> { null }
internal val LocalNativeContentTopPadding = compositionLocalOf<Dp?> { null }
