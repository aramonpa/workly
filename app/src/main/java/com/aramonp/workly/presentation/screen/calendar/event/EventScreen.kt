package com.aramonp.workly.presentation.screen.calendar.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.presentation.component.AlertDialogTask
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.DateTimePickerField
import com.aramonp.workly.presentation.component.LabeledField
import kotlinx.coroutines.launch

@Composable
fun EventScreen(calendarId: String, eventId: String, navController: NavHostController, viewModel: EventViewModel = hiltViewModel()) {
    val settingsState = viewModel.eventState.collectAsState()
    val teams = viewModel.teams.collectAsState()
    val titleError: String? by viewModel.titleError.collectAsState()
    val descriptionError: String? by viewModel.descriptionError.collectAsState()
    val datesError: String? by viewModel.datesError.collectAsState()
    val assigneeError: String? by viewModel.assigneeError.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(calendarId, eventId) {
        if (calendarId.isNotEmpty() && eventId.isNotEmpty()) {
            viewModel.fetchEvent(calendarId, eventId)
            viewModel.fetchTeams(calendarId)
        }
    }

    Scaffold(
        topBar = {
            EventTopBar(viewModel, navController)
        }
    ) {
        when (val state = settingsState.value) {
            is UiState.Loading -> {
                CircularProgress()
            }
            is UiState.Success<Event> -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    item {
                        LabeledField(
                            "Título",
                            state.data.title,
                            isError = titleError != null,
                            errorMessage = titleError
                        ) { value ->
                            coroutineScope.launch {
                                viewModel.onTitleChange(value)
                                viewModel.updateEventInfo()
                            }
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        LabeledField(
                            "Descripción",
                            state.data.description,
                            isError = descriptionError != null,
                            errorMessage = descriptionError
                        ) { value ->
                            coroutineScope.launch {
                                viewModel.onDescriptionChange(value)
                                viewModel.updateEventInfo()
                            }
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        DateTimePickerField(
                            label = "Fecha inicio",
                            dateLabel = "Fecha inicio",
                            timeLabel = "Hora inicio",
                            value = state.data.startDate,
                            onConfirmation = { value ->
                                coroutineScope.launch {
                                    viewModel.onStartDateChange(value)
                                    viewModel.updateEventInfo()
                                }
                            }
                        )
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        DateTimePickerField(
                            label = "Fecha fin",
                            dateLabel = "Fecha fin",
                            timeLabel = "Hora fin",
                            value = state.data.endDate,
                            onConfirmation = { value ->
                                coroutineScope.launch {
                                    viewModel.onEndDateChange(value)
                                    viewModel.updateEventInfo()
                                }
                            }
                        )
                        if (datesError != null) {
                            Text(datesError!!, color = Color.Red, fontSize = 12.sp)
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        LabeledField(
                            "Localización",
                            state.data.location,
                            isError = titleError != null,
                            errorMessage = titleError
                        ) { value ->
                            coroutineScope.launch {
                                viewModel.onLocationChange(value)
                                viewModel.updateEventInfo()
                            }
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    item {
                        LabeledDropDownMenu(
                            label = "Asignado",
                            value = state.data.assignee,
                            teams = teams.value,
                            errorMessage = assigneeError,
                            onConfirmation = { value ->
                                coroutineScope.launch {
                                    viewModel.onAssigneeChange(value)
                                    viewModel.updateEventInfo()
                                }
                            }
                        )
                    }
                }
            }
            is UiState.Error -> {
                Text(text = (settingsState.value as UiState.Error).message)
            }
        }
    }
}

@Composable
fun EventTopBar(viewModel: EventViewModel, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val showAlertDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        IconButton (
            onClick = {
                navController.popBackStack()
            }
        ) {
            Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
        }
        Spacer(modifier = Modifier.weight(1f))
        Box {
            IconButton (
                onClick = {
                    expanded = true
                }
            ) {
                Image(imageVector = Icons.Default.MoreVert, contentDescription = "Más")

            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        showAlertDialog.value = true
                    }
                )
            }
        }
        if (showAlertDialog.value) {
            AlertDialogTask(
                onDismissRequest = { showAlertDialog.value = false },
                onConfirmation = {
                    coroutineScope.launch {
                        viewModel.deleteEvent()
                        showAlertDialog.value = false
                        navController.popBackStack()
                    }
                },
                dialogTitle = "¿Está seguro?",
                dialogText = "La operación de eliminar un evento no se puede deshacer.",
                icon = Icons.Default.Info,
                iconDescription = "Aviso"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledDropDownMenu(label: String, value: String, teams: List<String>, onConfirmation: (String) -> Unit, errorMessage: String?) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(value) }

    val showDialog = remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontWeight = FontWeight.Bold)

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showDialog.value = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar"
                )
            }
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(label) },
                text = {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedText,
                            onValueChange = { selectedText = it },
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                            isError = errorMessage != null
                        )
                        if (errorMessage != null) {
                            Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                        }

                        val filteredOptions =
                            teams.filter { it.contains(selectedText, ignoreCase = true) }

                        if (filteredOptions.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                }
                            ) {
                                filteredOptions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            selectedText = item
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirmation(selectedText)
                            showDialog.value = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog.value = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}