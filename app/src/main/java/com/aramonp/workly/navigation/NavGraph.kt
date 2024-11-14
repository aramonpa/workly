package com.aramonp.workly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.aramonp.workly.presentation.screen.home.HomeScreen
import com.aramonp.workly.presentation.screen.home.HomeViewModel
import com.aramonp.workly.presentation.screen.login.LogInScreen
import com.aramonp.workly.presentation.screen.login.LogInViewModel
import com.aramonp.workly.presentation.screen.signup.SignUpScreen
import com.aramonp.workly.presentation.screen.calendar.CalendarScreen
import com.aramonp.workly.presentation.screen.signup.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navHostController: NavHostController, startDestination: String) {
    NavHost(navController = navHostController, startDestination = startDestination) {
        composable(Route.LogInScreen.route) {
            val logInViewModel: LogInViewModel = hiltViewModel()
            LogInScreen(
                onNavigateToSignUp = { navHostController.navigate(Route.SignUpScreen.route) },
                onNavigateToHome = { navHostController.navigate(Route.HomeScreen.route, ) },
                viewModel = logInViewModel
            )
        }
        composable(Route.SignUpScreen.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                onNavigateToLogIn = { navHostController.navigate(Route.LogInScreen.route) },
                onNavigateToHome = { navHostController.navigate(Route.HomeScreen.route) },
                viewModel = signUpViewModel
            )
        }
        composable(Route.HomeScreen.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToCalendar = { navHostController.navigate(Route.CalendarScreen.route) },
                viewModel = homeViewModel
            )
        }
        composable(Route.CalendarScreen.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CalendarScreen(id = id)
        }
    }
}