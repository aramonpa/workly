package com.aramonp.workly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aramonp.workly.presentation.screen.login.LoginScreen

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "login") {
        composable("login") {
            LoginScreen()
        }
    }
}