package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hansholz.bestenotenapp.main.ViewModel
import com.hansholz.bestenotenapp.screens.Grades
import com.hansholz.bestenotenapp.screens.Home
import com.hansholz.bestenotenapp.screens.Settings
import com.hansholz.bestenotenapp.screens.Stats
import com.hansholz.bestenotenapp.screens.SubjectsAndTeachers

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FragmentNavigation(
    viewModel: ViewModel,
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Fragment.Home.route,
            enterTransition = {
                val isBackwards = initialState.destination.route != navController.previousBackStackEntry?.destination?.route
                if (isBackwards) {
                    slideInHorizontally(tween()) { -it / 4 } + fadeIn()
                } else {
                    slideInHorizontally(tween()) { it }
                }
            },
            exitTransition = {
                val isBackwards = initialState.destination.route != navController.previousBackStackEntry?.destination?.route
                if (isBackwards) {
                    slideOutHorizontally(tween()) { it }
                } else {
                    slideOutHorizontally(tween()) { -it / 4 } + fadeOut()
                }
            }
        ) {
            composable(route = Fragment.Home.route) {
                Home(
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onNavigateToScreen = {
                        navController.navigate(it.route)
                    }
                )
            }

            composable(route = Fragment.Grades.route) {
                Grades(
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }

            composable(route = Fragment.SubjectsAndTeachers.route) {
                SubjectsAndTeachers(
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                )
            }

            composable(route = Fragment.Stats.route) {
                Stats()
            }

            composable(route = Fragment.Settings.route) {
                Settings(
                    viewModel = viewModel,
                    onNavigateToLogin = onNavigateToLogin
                )
            }
        }
    }
}