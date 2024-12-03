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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
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
                    Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                        R.string.back_text),)
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
                        stringResource(R.string.name_label),
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
                        stringResource(R.string.description_label),
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
                        label = stringResource(R.string.members_title),
                        title = stringResource(R.string.members_description)
                    ) { navController.navigate(Route.MemberScreen.createRoute(id)) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    DetailedList(
                        title = stringResource(R.string.teams_title),
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
            Image(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_team_text))

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
            dialogTitle = stringResource(R.string.new_team_title),
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
            Image(imageVector = Icons.Default.Clear, contentDescription = stringResource(R.string.delete_text))
        }
    }
    if (showAlertDialog.value) {
        AlertDialogTask(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = { onDelete(team) },
            dialogTitle = stringResource(R.string.asking_alert_text),
            dialogText = stringResource(R.string.alert_dialog_team_description),
            icon = Icons.Default.Info,
            iconDescription = stringResource(R.string.notice_text)
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
                        contentDescription = stringResource(R.string.go_members_text)
                    )
                }
            }
        }
    }
}