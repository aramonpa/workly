package com.aramonp.workly.presentation.screen.calendar.settings

import androidx.compose.runtime.LaunchedEffect
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.presentation.screen.profile.settings.CalendarSettingsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.screen.home.CalendarItem
import com.aramonp.workly.presentation.screen.home.HomeViewModel
import com.aramonp.workly.presentation.screen.home.RegisterField
import kotlinx.coroutines.launch

@Composable
fun CalendarSettingsScreen(id: String, navController: NavHostController, viewModel: CalendarSettingsViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            viewModel.fetchCalendar(id)
        }
    }

    val settingsState = viewModel.settingsState.collectAsState()

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
            is HomeState.Loading -> {
                CircularProgress()
            }
            is HomeState.Success<Calendar> -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    LabeledTextField("Nombre", state.data.name) { value -> viewModel.onNameChange(value) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledTextField("Descripción", state.data.description) { value -> viewModel.onDescriptionChange(value) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    TeamsList(
                        state.data.teams,
                        { showDialog = true },
                        {
                            coroutineScope.launch {
                                viewModel.deleteTeam(it)
                            }
                        })


                    if (showDialog) {
                        ShowDialogSurface(
                            viewModel,
                            onDismiss = { showDialog = false })
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateCalendarInfo()
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

@Composable
fun TeamsList(teams: List<String>, onClick: () -> Unit, onDelete: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Equipos de trabajo", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        IconButton (
            onClick = {
                onClick()
            }
        ) {
            Image(imageVector = Icons.Default.Add, contentDescription = "Añadir")

        }
    }
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(teams) { item ->
            TeamItem(item, onDelete)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun TeamItem(team: String, onDelete: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(team)
        Spacer(modifier = Modifier.weight(1f))
        IconButton (
            onClick = {
                onDelete(team)
            }
        ) {
            Image(imageVector = Icons.Default.Clear, contentDescription = "Eliminar")

        }
    }
}

@Composable
fun ShowDialogSurface(viewModel: CalendarSettingsViewModel, onDismiss: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    val name = remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nuevo equipo", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                RegisterField(
                    name.value,
                    "Nombre",
                    { name.value = it },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Row {
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = onDismiss
                    ) {
                        Text("Cerrar")
                    }
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.addTeam(name.value)
                                name.value = ""
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Crear")
                    }
                }

            }
        }
    }
}