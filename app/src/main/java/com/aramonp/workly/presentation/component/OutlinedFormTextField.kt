package com.aramonp.workly.presentation.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun OutlinedFormTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOption: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String?
) {
    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOption,
        visualTransformation = visualTransformation,
        isError = isError
    )
    if (errorMessage != null) {
        Text(errorMessage, color = Color.Red, fontSize = 12.sp)
    }
}