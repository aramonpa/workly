package com.aramonp.workly.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aramonp.workly.presentation.screen.home.HomeScreen
import com.aramonp.workly.presentation.screen.home.HomeViewModel
import com.aramonp.workly.presentation.screen.login.LogInScreen
import com.aramonp.workly.presentation.screen.login.LogInViewModel
import com.aramonp.workly.presentation.screen.signup.SignUpScreen
import com.aramonp.workly.presentation.screen.calendar.CalendarScreen
import com.aramonp.workly.presentation.screen.calendar.CalendarViewModel
import com.aramonp.workly.presentation.screen.calendar.event.EventScreen
import com.aramonp.workly.presentation.screen.calendar.event.EventViewModel
import com.aramonp.workly.presentation.screen.calendar.settings.CalendarSettingsScreen
import com.aramonp.workly.presentation.screen.profile.ProfileScreen
import com.aramonp.workly.presentation.screen.profile.ProfileViewModel
import com.aramonp.workly.presentation.screen.profile.settings.CalendarSettingsViewModel
import com.aramonp.workly.presentation.screen.profile.settings.SettingsScreen
import com.aramonp.workly.presentation.screen.profile.settings.SettingsViewModel
import com.aramonp.workly.presentation.screen.signup.SignUpViewModel

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
                navHostController,
                viewModel = homeViewModel
            )
        }
        composable(Route.ProfileScreen.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                navHostController,
                viewModel = profileViewModel
                )
        }
        composable(Route.SettingsScreen.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                navHostController,
                viewModel = settingsViewModel
            )
        }
        composable(Route.CalendarSettingsScreen.route) { backStackEntry ->
            val settingsViewModel: CalendarSettingsViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CalendarSettingsScreen(
                id = id,
                navHostController,
                viewModel = settingsViewModel
            )
        }
        composable(Route.CalendarScreen.route) { backStackEntry ->
            val calendarViewModel: CalendarViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CalendarScreen(calendarId = id, calendarViewModel, navHostController)
        }
        composable(Route.EventScreen.route) { backStackEntry ->
            val eventViewModel: EventViewModel = hiltViewModel()
            val calendarId = backStackEntry.arguments?.getString("calendarId") ?: ""
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventScreen(calendarId = calendarId, eventId = eventId, navHostController, eventViewModel)
        }
    }
}