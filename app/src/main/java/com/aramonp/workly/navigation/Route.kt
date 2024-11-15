package com.aramonp.workly.navigation

sealed class Route (val route: String) {
    data object LogInScreen : Route(route = "logInScreen")
    data object SignUpScreen : Route(route = "signUpScreen")
    data object HomeScreen : Route(route = "homeScreen")
    data object CalendarScreen : Route(route = "calendarScreen/{id}")
    data object Settings : Route(route = "settingsScreen")
}