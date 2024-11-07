package com.aramonp.workly.presentation.screen.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.presentation.screen.login.LogInViewModel

@Composable
fun HomeScreen(onNavigateToLogIn: () -> Unit = {}, viewModel: LogInViewModel) {
    val authState = viewModel.authState.observeAsState()

    // Manejo del estado de autenticación
    when (authState.value) {
        is AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                onNavigateToLogIn()
            }
        }
        else -> Unit /* Estado Idle, no hacer nada */
    }
    Text("EN LA HOME")
}