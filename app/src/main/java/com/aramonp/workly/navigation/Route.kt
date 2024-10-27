package com.aramonp.workly.navigation

sealed class Route (val route: String) {
    object LogInScreen : Route(route = "logInScreen")
    object SignUpScreen : Route(route = "signUpScreen")
    object HomeScreen : Route(route = "homeScreen")
}