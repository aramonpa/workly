package com.aramonp.workly.presentation.screen.calendar.settings

import androidx.compose.runtime.LaunchedEffect
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.presentation.screen.profile.settings.CalendarSettingsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.component.AlertDialogTask
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.LabeledField
import com.aramonp.workly.presentation.component.OutlinedFormTextField
import com.aramonp.workly.presentation.component.OutlinedTextFieldDialog
import kotlinx.coroutines.launch

@Composable
fun CalendarSettingsScreen(id: String, navController: NavHostController, viewModel: CalendarSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val calendarSettingsFormState by viewModel.calendarSettingsFormState.collectAsState()
    val validationState = viewModel.validationState.collectAsState()

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
            is UiState.Loading -> {
                CircularProgress()
            }
            is UiState.Success<Calendar> -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    //TODO: Fix values that are modified when exist errors.
                    LabeledField(
                        "Nombre",
                        state.data.name,
                        isError = calendarSettingsFormState.nameError != null,
                        errorMessage = calendarSettingsFormState.nameError,
                        validationState = validationState,
                        onDismiss = {

                        }
                    ) { value ->
                        coroutineScope.launch {
                            viewModel.onNameChange(value)
                            viewModel.updateCalendarInfo()
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    LabeledField(
                        "Descripción",
                        state.data.description,
                        isError = calendarSettingsFormState.descriptionError != null,
                        errorMessage = calendarSettingsFormState.descriptionError,
                        validationState = validationState,
                        onDismiss = {

                        }
                    ) { value ->
                        coroutineScope.launch {
                            viewModel.onDescriptionChange(value)
                            viewModel.updateCalendarInfo()
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    MemberField(
                        label = "Miembros",
                        title = "Modifica miembros del calendario"
                    ) { navController.navigate(Route.MemberScreen.createRoute(id)) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    DetailedList(
                        title = "Equipos de trabajo",
                        items = state.data.teams,
                        validationState = validationState,
                        onDelete = {
                            coroutineScope.launch {
                                viewModel.deleteTeam(it)
                            }
                        },
                        onConfirmation = {
                            coroutineScope.launch {
                                viewModel.addTeam(it)
                            }
                        },
                        errorMessage = calendarSettingsFormState.teamError
                    )
                }
            }
            is UiState.Error -> {
                Text(text = (settingsState.value as UiState.Error).message)
            }
        }

    }
}

@Composable
fun DetailedList(
    title: String,
    items: List<String>,
    validationState: State<Boolean>,
    onDelete: (String) -> Unit,
    onConfirmation: (String) -> Unit,
    errorMessage: String?
    ) {
    val showAlertDialog = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        IconButton (
            onClick = {
                showAlertDialog.value = true
            }
        ) {
            Image(imageVector = Icons.Default.Add, contentDescription = "Añadir")

        }
    }
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            DetailedItem(item, onDelete)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }

    if (showAlertDialog.value) {
        OutlinedTextFieldDialog(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = {
                if (validationState.value) {
                    showAlertDialog.value = false
                }
                onConfirmation(it)
            },
            dialogTitle = "Nuevo equipo de trabajo",
            dialogText = "",
            isError = errorMessage != null,
            errorMessage = errorMessage
        )
    }
}

@Composable
fun DetailedItem(team: String, onDelete: (String) -> Unit) {
    val showAlertDialog = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(team)
        Spacer(modifier = Modifier.weight(1f))
        IconButton (
            onClick = { showAlertDialog.value = true }
        ) {
            Image(imageVector = Icons.Default.Clear, contentDescription = "Eliminar")
        }
    }
    if (showAlertDialog.value) {
        AlertDialogTask(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = { onDelete(team) },
            dialogTitle = "¿Está seguro?",
            dialogText = "La operación de eliminar un equipo no se puede deshacer.",
            icon = Icons.Default.Info,
            iconDescription = "Aviso"
        )
    }
}

@Composable
fun MemberField(label: String, title: String, onClick: () -> Unit) {
    Column {
        Text(text = label, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier.clickable { onClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Ir"
                    )
                }
            }
        }
    }
}