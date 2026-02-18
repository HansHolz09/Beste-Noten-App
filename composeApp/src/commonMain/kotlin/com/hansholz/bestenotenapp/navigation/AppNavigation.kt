package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hansholz.bestenotenapp.main.LocalRequireBiometricAuthentification
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.screens.biometry.Biometry
import com.hansholz.bestenotenapp.screens.login.Login
import com.hansholz.bestenotenapp.security.kSafe
import eu.anifantakis.lib.ksafe.compose.mutableStateOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    viewModel: ViewModel,
    onNavHostReady: suspend (NavController) -> Unit = {},
) {
    val kSafe = remember { kSafe() }
    val requireBiometricAuthentification by LocalRequireBiometricAuthentification.current
    val token by kSafe.mutableStateOf("", "authToken")
    val studentId by kSafe.mutableStateOf("", "studentId")
    val startDestination =
        rememberSaveable {
            when {
                token.isEmpty() || studentId.isEmpty() -> Screen.Login.route
                requireBiometricAuthentification -> Screen.Biometry.route
                else -> Screen.Main.route
            }
        }
    val navController = rememberNavController()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
        ) {
            composable(route = Screen.Biometry.route) {
                Biometry(
                    viewModel = viewModel,
                    onNavigateToScreen = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }

            composable(route = Screen.Login.route) {
                Login(
                    viewModel = viewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
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
                    },
                )
            }
        }
    }
}
