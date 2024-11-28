package com.aramonp.workly.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aramonp.workly.util.combineDateAndTime
import com.aramonp.workly.util.convertToTimestamp
import com.aramonp.workly.util.getDateFromTimeStamp
import com.aramonp.workly.util.getTimeFromTimeStamp
import com.google.firebase.Timestamp

@Composable
fun DateTimePickerField(
    label: String,
    dateLabel: String,
    timeLabel: String,
    value: Timestamp,
    onConfirmation: (Timestamp) -> Unit
) {
    val date = getDateFromTimeStamp(value)
    val time = getTimeFromTimeStamp(value)

    var selectedDate by rememberSaveable { mutableStateOf(date) }
    var selectedTime by rememberSaveable { mutableStateOf(time) }
    val showDialog = remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontWeight = FontWeight.Bold)

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.toDate().toString(),
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
                    Column {
                        Row {
                            DatePickerField(
                                value = selectedDate,
                                label = dateLabel,
                                onDateSelected = {selectedDate = it}
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TimePickerField(
                                value = selectedTime,
                                label = timeLabel,
                                onTimeSelected = {selectedTime = it}
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirmation(convertToTimestamp(combineDateAndTime(selectedDate, selectedTime)))
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