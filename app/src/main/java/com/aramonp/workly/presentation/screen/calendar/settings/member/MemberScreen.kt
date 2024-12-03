package com.aramonp.workly.presentation.screen.calendar.settings.member

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.presentation.component.AlertDialogTask
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.OutlinedTextFieldDialog
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
                    Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                        R.string.back_text))
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
                        title = stringResource(R.string.calendar_members_title),
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
            Image(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_member_description))

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
            dialogTitle = stringResource(R.string.new_member_dialog_title),
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
           Text(stringResource(R.string.owner_tag))
        } else {
            IconButton (
                onClick = { showAlertDialog.value = true }
            ) {
                Image(imageVector = Icons.Default.Clear, contentDescription = stringResource(R.string.delete_text))
            }
        }
    }
    if (showAlertDialog.value) {
        AlertDialogTask(
            onDismissRequest = { showAlertDialog.value = false },
            onConfirmation = { onDelete(member) },
            dialogTitle = stringResource(R.string.asking_alert_text),
            dialogText = stringResource(R.string.altert_dialog_member_description),
            icon = Icons.Default.Info,
            iconDescription = stringResource(R.string.notice_text)
        )
    }
}