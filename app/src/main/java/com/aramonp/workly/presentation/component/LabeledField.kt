package com.aramonp.workly.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LabeledField(label: String, value: String, isError: Boolean, errorMessage: String?, validationState: State<Boolean>, onDismiss: () -> Unit, onConfirmation: (String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(validationState.value) {
        if (validationState.value) {
            showDialog.value = false
        }
    }

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
            OutlinedTextFieldDialog(
                onDismissRequest = {
                    showDialog.value = false
                    onDismiss()
                },
                onConfirmation = {
                    onConfirmation(it)
                },
                dialogTitle = label,
                dialogText = value,
                isError = isError,
                errorMessage = errorMessage
            )
        }
    }
}