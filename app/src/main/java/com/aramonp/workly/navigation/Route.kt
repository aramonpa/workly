package com.aramonp.workly.navigation

sealed class Route (val route: String) {
    data object LogInScreen : Route(route = "logInScreen")
    data object SignUpScreen : Route(route = "signUpScreen")
    data object HomeScreen : Route(route = "homeScreen")
    data object CalendarScreen : Route(route = "calendarScreen/{id}") {
        fun createRoute(id: String): String {
            return "calendarScreen/$id"
        }
    }
    data object EventScreen : Route(route = "eventScreen/{calendarId}/{eventId}") {
        fun createRoute(calendarId: String, eventId: String): String {
            return "eventScreen/$calendarId/$eventId"
        }
    }
    data object ProfileScreen : Route(route = "profileScreen")
    data object SettingsScreen : Route(route = "settingsScreen")
    data object CalendarSettingsScreen : Route(route = "calendarSettingsScreen/{id}") {
        fun createRoute(id: String): String {
            return "calendarSettingsScreen/$id"
        }
    }
}