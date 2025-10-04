package com.hansholz.bestenotenapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
expect fun topAppBarStartPadding(sideMenuExpanded: Boolean): Dp

@Composable
expect fun topAppBarEndPadding(): Dp
