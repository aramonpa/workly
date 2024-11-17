package com.aramonp.workly.presentation.screen.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.navigation.BottomNavigationItem
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.component.CircularProgress
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SettingsViewModel) {
    val settingsState = viewModel.settingsState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                ) {
                    Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        }
    ) {
        when (val state = settingsState.value) {
            is HomeState.Loading -> {
                CircularProgress()
            }
            is HomeState.Success<User> -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    LabeledTextField("Nombre", state.data.name) { value -> viewModel.onNameChange(value) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledTextField("Apellidos", state.data.surname) { value -> viewModel.onSurnameChange(value) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledTextField("Nombre de usuario", state.data.username) { value -> viewModel.onUsernameChange(value) }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateUserInfo()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("Guardar")
                    }
                }
            }
            is HomeState.Error -> {
                Text(text = (settingsState.value as HomeState.Error).message)
            }
        }

    }
}

@Composable
fun LabeledTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}