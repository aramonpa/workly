package com.aramonp.workly.presentation.screen.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.LabeledField
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SettingsViewModel = hiltViewModel()) {
    val settingsState = viewModel.settingsState.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val surnameError by viewModel.surnameError.collectAsState()
    val userNameError by viewModel.userNameError.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(top = 32.dp)
            ) {
                IconButton (
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        }
    ) {
        when (val state = settingsState.value) {
            is UiState.Loading -> {
                CircularProgress()
            }
            is UiState.Success<User> -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    LabeledField(
                        "Nombre",
                        state.data.name,
                        isError = nameError != null,
                        errorMessage = nameError
                    ) { value ->
                        coroutineScope.launch {
                            viewModel.onNameChange(value)
                            viewModel.updateUserInfo()
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledField(
                        "Apellidos",
                        state.data.surname,
                        isError = surnameError != null,
                        errorMessage = surnameError
                    ) { value ->
                        coroutineScope.launch {
                            viewModel.onSurnameChange(value)
                            viewModel.updateUserInfo()
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledField(
                        "Nombre de usuario",
                        state.data.username,
                        isError = userNameError != null,
                        errorMessage = userNameError
                    ) { value ->
                        coroutineScope.launch {
                            viewModel.onUsernameChange(value)
                            viewModel.updateUserInfo()
                        }
                    }
                }
            }
            is UiState.Error -> {
                Text(text = (settingsState.value as UiState.Error).message)
            }
        }

    }
}