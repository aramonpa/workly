package com.aramonp.workly.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.CalendarFormState
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.component.BottomNavigationBar
import com.aramonp.workly.presentation.component.OutlinedFormTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onNavigateToCalendar: (String) -> Unit, navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val userState = viewModel.userState.collectAsState()
    val calendarListState = viewModel.calendarListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUser()
        viewModel.fetchUserCalendars()
    }

    when (userState.value) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            HomeContent(
                (userState.value as UiState.Success<User>).data,
                calendarListState.value,
                viewModel,
                onNavigateToCalendar,
                navController
            )
        }
        is UiState.Error -> {
            Text(stringResource(R.string.loading_error_text), textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    user: User,
    calendarListState: UiState<List<Calendar>>,
    viewModel: HomeViewModel,
    navigation: (String) -> Unit,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val calendarFormState by viewModel.calendarFormState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    stringResource(R.string.greeting_text),
                    fontSize = 15.sp,
                )
                Text(
                    user.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.add_calendar_description))
            }
        }
    ) {
        if (showDialog) {
            ShowDialogSurface(viewModel, calendarFormState, onDismiss = { showDialog = false })
        }

        when (calendarListState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success  -> {
                PullToRefreshBox(
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        coroutineScope.launch {
                            isRefreshing = true
                            viewModel.fetchUserCalendars()
                            isRefreshing = false
                        }
                    }
                ) {
                    CalendarList(
                        Modifier
                            .padding(it)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        calendarListState.data.size,
                        calendarListState.data,
                        navController
                    )
                }
            }
            is UiState.Error -> {
                Text(text = calendarListState.message)
            }
        }
    }
}

@Composable
fun CalendarList(
    modifier: Modifier = Modifier,
    calendarNum: Int,
    calendarList: List<Calendar>,
    navController: NavHostController) {
    Column (modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.calendar_list_title),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.size(10.dp))
            Box(
                modifier = Modifier
                    .background(Color.LightGray, shape = CircleShape)
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = calendarNum.toString(),
                    fontSize = 20.sp
                )
            }
        }
        if (calendarNum == 0) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.no_calendars_text),
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(calendarList) { item ->
                    CalendarItem(item.name, item.description, item.uid, navController)
                }
            }
        }
    }
}

@Composable
fun CalendarItem(name: String, description: String, id: String, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clickable { navController.navigate(Route.CalendarScreen.createRoute(id)) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text(
                    text = description,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = null
                )
            }
            Row {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ShowDialogSurface(viewModel: HomeViewModel, calendarFormState: CalendarFormState, onDismiss: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    val validationState by viewModel.validationState.collectAsState()

    Dialog(
        onDismissRequest = {
            viewModel.clearErrors()
            viewModel.clearFields()
            onDismiss()
        }
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.new_calendar_title), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedFormTextField(
                    calendarFormState.name,
                    stringResource(R.string.name_label),
                    { value -> viewModel.onNameChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = calendarFormState.nameError != null,
                    errorMessage = calendarFormState.nameError
                )
                OutlinedFormTextField(
                    calendarFormState.description,
                    stringResource(R.string.description_label),
                    { value -> viewModel.onDescriptionChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = calendarFormState.descriptionError != null,
                    errorMessage = calendarFormState.descriptionError
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            viewModel.clearErrors()
                            viewModel.clearFields()
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.dismiss_dialog_text))
                    }
                    TextButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.createCalendar()
                                if (validationState) {
                                    viewModel.clearFields()
                                    onDismiss()
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.confirm_dialog_text))
                    }
                }

            }
        }
    }
}