package com.aramonp.workly.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aramonp.workly.navigation.BottomNavigationItem

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Profile
    )

    NavigationBar {
        items.forEachIndexed { _, item ->
            NavigationBarItem(
                selected = currentDestination?.route == item.route,
                onClick = {
                    //TODO: Check if every click is making a request
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        if (currentDestination?.route == item.route) {item.icon} else {item.outlinedIcon},
                        contentDescription = item.title
                    )},
                label = { Text(item.title) }
            )
        }
    }
}