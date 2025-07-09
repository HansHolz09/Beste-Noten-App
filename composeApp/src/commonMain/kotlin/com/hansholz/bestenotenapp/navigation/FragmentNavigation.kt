package com.hansholz.bestenotenapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
            startDestination = Fragment.Home.route
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