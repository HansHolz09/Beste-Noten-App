package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.screens.Login

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    viewModel: ViewModel,
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.authTokenManager.getToken().isNullOrEmpty()) Screen.Login.route else Screen.Main.route
        ) {
            composable(route = Screen.Login.route) {
                Login(
                    viewModel = viewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Main.route)
                        navController.clearBackStack(Screen.Login.route)
                    }
                )
            }

            composable(route = Screen.Main.route) {
                AppNavigationDrawer(
                    viewModel = viewModel,
                    onNavHostReady = onNavHostReady,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                        navController.clearBackStack(Screen.Main.route)
                    }
                )
            }
        }
    }
}