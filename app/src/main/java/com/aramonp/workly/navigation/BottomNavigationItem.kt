package com.aramonp.workly.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(val route: String, val icon: ImageVector, val outlinedIcon: ImageVector, val title: String) {
    data object Home : BottomNavigationItem(Route.HomeScreen.route, Icons.Default.Home, Icons.Outlined.Home, "Home")
    data object Profile : BottomNavigationItem(Route.ProfileScreen.route, Icons.Default.AccountCircle, Icons.Outlined.AccountCircle, "Perfil")
}