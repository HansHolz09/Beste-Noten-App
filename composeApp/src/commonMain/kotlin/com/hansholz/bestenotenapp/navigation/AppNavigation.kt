package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.screens.Biometry
import com.hansholz.bestenotenapp.screens.Login

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    viewModel: ViewModel,
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    val requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val token = rememberSaveable { viewModel.authTokenManager.getToken() ?: "" }
    val startDestination = rememberSaveable {
        when {
            requireBiometricAuthentification && !token.isEmpty() -> Screen.Biometry.route
            token.isEmpty() -> Screen.Login.route
            else -> Screen.Main.route
        }
    }
    val navController = rememberNavController()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(route = Screen.Biometry.route) {
                Biometry(
                    viewModel = viewModel,
                    onNavigateToScreen = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Login.route) {
                Login(
                    viewModel = viewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Main.route) {
                AppNavigationDrawer(
                    viewModel = viewModel,
                    onNavHostReady = onNavHostReady,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}