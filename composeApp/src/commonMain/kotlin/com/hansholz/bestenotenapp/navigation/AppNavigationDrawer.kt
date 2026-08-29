package com.hansholz.bestenotenapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.hansholz.bestenotenapp.components.ConfettiPresets
import com.hansholz.bestenotenapp.components.NavigationDrawer
import com.hansholz.bestenotenapp.components.enhanced.EnhancedVibrations
import com.hansholz.bestenotenapp.components.enhanced.enhancedVibrateN
import com.hansholz.bestenotenapp.main.LocalNativeComponentsEnabled
import com.hansholz.bestenotenapp.main.LocalNavigationDrawerTopPadding
import com.hansholz.bestenotenapp.main.ViewModel
import io.github.vinceglb.confettikit.compose.ConfettiKit
import kotlinx.coroutines.launch
import top.ltfan.multihaptic.compose.rememberVibrator

@Composable
fun AppNavigationDrawer(
    viewModel: ViewModel,
    onNavHostReady: suspend (NavController) -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onDestinationChanged: (String) -> Unit = {},
    onCanNavigateBackChanged: (Boolean) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val vibrator = rememberVibrator()
    val isCompactWindow =
        !currentWindowAdaptiveInfoV2()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    var showConfetti by remember { mutableStateOf(false) }

    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentRoute?.destination?.route) {
        currentRoute?.destination?.route?.let(onDestinationChanged)
        onCanNavigateBackChanged(navController.previousBackStackEntry != null)
    }
    val nativeComponentsEnabled = LocalNativeComponentsEnabled.current.value
    val navigationDrawerTopPadding = LocalNavigationDrawerTopPadding.current ?: 15.dp
    val screenContent: @Composable () -> Unit = {
        FragmentNavigation(
            viewModel = viewModel,
            navController = navController,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
    if (!nativeComponentsEnabled) {
        NavigationDrawer(
            compactDrawerState = viewModel.compactDrawerState.value,
            mediumExpandedDrawerState = viewModel.mediumExpandedDrawerState.value,
            hazeState = viewModel.hazeBackgroundState,
            drawerContent = {
                AppDrawerContent(
                    selectedRoute = currentRoute?.destination?.route,
                    topPadding = navigationDrawerTopPadding,
                    animateLogo = viewModel.compactDrawerState.value.isOpen,
                    onDestinationSelected = { screen ->
                        scope.launch {
                            navController.navigate(screen.route)
                            if (isCompactWindow) viewModel.closeOrOpenDrawer(isCompactWindow)
                        }
                        vibrator.enhancedVibrateN(EnhancedVibrations.CLICK)
                    },
                    onLogoClick = {
                        if (!showConfetti) {
                            showConfetti = true
                            vibrator.enhancedVibrateN(EnhancedVibrations.LOGO_RAIN)
                        }
                    },
                )
            },
            content = screenContent,
        )
    } else {
        screenContent()
    }
    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    if (showConfetti) {
        ConfettiKit(
            modifier = Modifier.fillMaxSize(),
            parties = ConfettiPresets.logos(),
            onParticleSystemEnded = { _, activeSystems ->
                if (activeSystems == 0) showConfetti = false
            },
        )
    }
}
