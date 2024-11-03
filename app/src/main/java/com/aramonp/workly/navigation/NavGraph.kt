package com.aramonp.workly.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.aramonp.workly.presentation.screen.home.HomeScreen
import com.aramonp.workly.presentation.screen.login.LogInScreen
import com.aramonp.workly.presentation.screen.login.LogInViewModel
import com.aramonp.workly.presentation.screen.signup.SignUpScreen
import com.aramonp.workly.presentation.screen.signup.SignUpViewModel

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Route.LogInScreen.route) {
        composable(Route.LogInScreen.route) {
            val logInViewModel: LogInViewModel = hiltViewModel()
            LogInScreen(
                onNavigateToSignUp = { navHostController.navigate(Route.SignUpScreen.route) },
                onNavigateToHome = { navHostController.navigate(Route.HomeScreen.route) },
                viewModel = logInViewModel
            )
        }
        composable(Route.SignUpScreen.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                onNavigateToLogIn = { navHostController.navigate(Route.LogInScreen.route) },
                viewModel = signUpViewModel
            )
        }
        composable(Route.HomeScreen.route) {
            val logInViewModel: LogInViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToLogIn = { navHostController.navigate(Route.LogInScreen.route) },
                viewModel = logInViewModel
            )
        }
    }
}