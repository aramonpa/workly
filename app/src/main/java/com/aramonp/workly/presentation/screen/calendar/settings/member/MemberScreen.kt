package com.aramonp.workly.presentation.screen.calendar.settings.member

import android.util.Log
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.presentation.component.AlertDialogTask
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.LabeledField
import com.aramonp.workly.presentation.component.OutlinedFormTextField
import com.aramonp.workly.presentation.component.OutlinedTextFieldDialog
import com.aramonp.workly.presentation.screen.profile.settings.CalendarSettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun MemberScreen(id: String, navController: NavHostController, viewModel: MemberViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val nameError = viewModel.nameError.collectAsState()
    val memberState = viewModel.memberState.collectAsState()

    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            viewModel.fetchMembers(id)
        }
    }

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
        when (val state = memberState.value) {
            is UiState.Loading -> {
                CircularProgress()
            }
            is UiState.Success<List<String>> -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    DetailedList(
                        title = "Miembros del calendario",
                        items = state.data,
                        onDelete = { value ->
                            coroutineScope.launch {
                                viewModel.deleteMember(value)
                            }
                        },
                        onConfirmation = { value ->
                            coroutineScope.launch {
                                viewModel.addMember(value)
                            }
                        },
                        errorMessage = nameError.value
                    )
                }
            }
            is UiState.Error -> {
                Text(text = (memberState.value as UiState.Error).message)
            }
        }

    }
}

@Composable
fun DetailedList(
    title: String,
    items: List<String>,
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
        itemsIndexed(items) { index, item ->
            DetailedItem(index, item, onDelete)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }

    if (showAlertDialog.value) {
        OutlinedTextFieldDialog(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = {
                onConfirmation(it)
            },
            dialogTitle = "Nuevo miembro",
            dialogText = "",
            isError = errorMessage != null,
            errorMessage = errorMessage
        )
    }
}

@Composable
fun DetailedItem(index: Int, member: String, onDelete: (String) -> Unit) {
    val showAlertDialog = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(member)
        Spacer(modifier = Modifier.weight(1f))

        if (index == 0) {
           Text("(propietario)")
        } else {
            IconButton (
                onClick = { showAlertDialog.value = true }
            ) {
                Image(imageVector = Icons.Default.Clear, contentDescription = "Eliminar")
            }
        }
    }
    if (showAlertDialog.value) {
        AlertDialogTask(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = { onDelete(member) },
            dialogTitle = "¿Está seguro?",
            dialogText = "La operación de eliminar un miembro no se puede deshacer.",
            icon = Icons.Default.Info,
            iconDescription = "Aviso"
        )
    }
}

@Composable
fun ShowDialogSurface(viewModel: MemberViewModel, onDismiss: () -> Unit = {}, errorMessage: String?) {
    val coroutineScope = rememberCoroutineScope()
    val name = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nuevo miembro", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedFormTextField(
                    name.value,
                    "Nombre",
                    { name.value = it },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = errorMessage != null,
                    errorMessage = errorMessage
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
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
                                viewModel.addMember(name.value)
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