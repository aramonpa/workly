package com.aramonp.workly.presentation.screen.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.aramonp.workly.presentation.component.BottomNavigationBar

@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Text(modifier = Modifier.padding(it), text = "hheheheh", textAlign = TextAlign.Center)
    }
}