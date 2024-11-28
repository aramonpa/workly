package com.aramonp.workly.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aramonp.workly.presentation.screen.home.HomeScreen
import com.aramonp.workly.presentation.screen.login.LogInScreen
import com.aramonp.workly.presentation.screen.signup.SignUpScreen
import com.aramonp.workly.presentation.screen.calendar.CalendarScreen
import com.aramonp.workly.presentation.screen.calendar.event.EventScreen
import com.aramonp.workly.presentation.screen.calendar.settings.CalendarSettingsScreen
import com.aramonp.workly.presentation.screen.profile.ProfileScreen
import com.aramonp.workly.presentation.screen.profile.settings.SettingsScreen

@Composable
fun NavGraph(navHostController: NavHostController, startDestination: String) {
    NavHost(navController = navHostController, startDestination = startDestination) {
        composable(Route.LogInScreen.route) {
            LogInScreen(
                onNavigateToSignUp = { navHostController.navigate(Route.SignUpScreen.route) },
                onNavigateToHome = { navHostController.navigate(Route.HomeScreen.route) },
            )
        }
        composable(Route.SignUpScreen.route) {
            SignUpScreen(
                onNavigateToLogIn = { navHostController.navigate(Route.LogInScreen.route) },
                onNavigateToHome = { navHostController.navigate(Route.HomeScreen.route) }
            )
        }
        composable(Route.HomeScreen.route) {
            HomeScreen(
                onNavigateToCalendar = { navHostController.navigate(Route.CalendarScreen.route) },
                navHostController
            )
        }
        composable(Route.ProfileScreen.route) {
            ProfileScreen(navHostController)
        }
        composable(Route.SettingsScreen.route) {
            SettingsScreen(navHostController)
        }
        composable(Route.CalendarSettingsScreen.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CalendarSettingsScreen(
                id = id,
                navHostController
            )
        }
        composable(Route.CalendarScreen.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            CalendarScreen(
                calendarId = id,
                navController = navHostController
            )
        }
        composable(Route.EventScreen.route) { backStackEntry ->
            val calendarId = backStackEntry.arguments?.getString("calendarId") ?: ""
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventScreen(calendarId = calendarId, eventId = eventId, navHostController)
        }
    }
}