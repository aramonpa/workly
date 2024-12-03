package com.aramonp.workly.presentation.screen.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.component.AlertDialogTask
import com.aramonp.workly.presentation.component.DatePickerField
import com.aramonp.workly.presentation.component.OutlinedFormTextField
import com.aramonp.workly.presentation.component.TimePickerField
import com.aramonp.workly.util.combineDateAndTime
import com.aramonp.workly.util.getDateFromTimeStamp
import com.aramonp.workly.util.getTimeFromTimeStamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(calendarId: String, navController: NavHostController, viewModel: CalendarViewModel = hiltViewModel()) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val calendarState = viewModel.calendarState.collectAsState()
    val eventsState = viewModel.eventsState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(calendarId) {
        if (calendarId.isNotEmpty()) {
            viewModel.fetchCalendar(calendarId)
            viewModel.fetchEvents()
        }
    }

    Scaffold(
        topBar = {
            CalendarTopBar(calendarId, viewModel, navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.add_calendar_description))
            }
        }
    ) {
        when (val state = calendarState.value) {
            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    CalendarContent(viewModel)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    when (val events = eventsState.value) {
                        is UiState.Success -> {
                            PullToRefreshBox(
                                state = refreshState,
                                isRefreshing = isRefreshing,
                                onRefresh = {
                                    coroutineScope.launch {
                                        isRefreshing = true
                                        viewModel.fetchEvents()
                                        isRefreshing = false
                                    }
                                }
                            ) {
                                if (events.data.isNullOrEmpty()) {
                                    Text(stringResource(R.string.no_events_text))
                                } else {
                                    EventContent(
                                        Modifier
                                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                                        events.data
                                    ) { eventId ->
                                        navController.navigate(
                                            Route.EventScreen.createRoute(
                                                calendarId,
                                                eventId
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        else -> Unit
                    }

                    if (showDialog) {
                        ShowDialogSurface(
                            viewModel,
                            state.data,
                            onDismiss = {
                                showDialog = false
                                viewModel.clearFields()
                            }
                        )
                    }
                }
            }
            is UiState.Error -> {
                Text(state.message)
            }
            else -> Unit
        }
    }
}

@Composable
fun CalendarTopBar(id: String, viewModel: CalendarViewModel, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    val showAlertDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
            Image(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_text))
        }
        Spacer(modifier = Modifier.weight(1f))
        Box {
            IconButton (
                onClick = {
                    expanded = true
                }
            ) {
                Image(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_text))

            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_title)) },
                    onClick = {
                        expanded = false
                        navController.navigate(Route.CalendarSettingsScreen.createRoute(id))
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete_text)) },
                    onClick = {
                        expanded = false
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
                        viewModel.deleteCalendar()
                        showAlertDialog.value = false
                        navController.popBackStack()
                    }
                },
                dialogTitle = stringResource(R.string.asking_alert_text),
                dialogText = stringResource(R.string.alert_dialog_calendar_description),
                icon = Icons.Default.Info,
                iconDescription = stringResource(R.string.notice_text)
            )
        }
    }
}

@Composable
fun CalendarContent(viewModel: CalendarViewModel) {
    val currentMonth = viewModel.currentMonth
    val selectedDate = viewModel.selectedDate
    val daysInMonth = viewModel.getDaysInMonth()
    val coroutineScope = rememberCoroutineScope()

    MonthNavigation(
        monthName = viewModel.getCurrentMonthName(),
        currentMonth = currentMonth,
        onMonthChanged = { newMonth ->
            viewModel.changeMonth(newMonth)  // Cambiar el mes desde el ViewModel
        }
    )

    // Mostrar los días de la semana (Lun, Mar, Mié, ...)
    WeekdaysHeader()

    // Mostrar los días del mes
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(daysInMonth) { day ->
            val textColor = when {
                day.month != currentMonth.month -> Color.Gray
                selectedDate == day -> Color.White
                else -> Color.Black
            }

            DayItem(
                day = day,
                selected = selectedDate == day,
                color = textColor
            ) {
                coroutineScope.launch {
                    viewModel.changeSelectedDay(day)  // Cambiar la fecha seleccionada desde el ViewModel
                }
            }
        }
    }
}

@Composable
fun MonthNavigation(monthName: String, currentMonth: YearMonth, onMonthChanged: (YearMonth) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Flecha para retroceder al mes anterior
        IconButton(onClick = {
            onMonthChanged(currentMonth.minusMonths(1))
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
        }

        // Título del mes
        Text(
            text = monthName + " " + currentMonth.year,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Flecha para avanzar al siguiente mes
        IconButton(onClick = {
            onMonthChanged(currentMonth.plusMonths(1))
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Siguiente mes")
        }
    }
}

@Composable
fun WeekdaysHeader() {
    val weekdays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        weekdays.forEach { weekday ->
            Text(
                text = weekday,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DayItem(day: LocalDate, color: Color, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Color.Gray else Color.Transparent
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .clickable { onClick() }
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

@Composable
fun ShowDialogSurface(viewModel: CalendarViewModel, calendar: Calendar, onDismiss: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            EventForm(viewModel, calendar, onDismiss)
        }
    }
}

@Composable
fun EventForm(viewModel: CalendarViewModel, calendar: Calendar, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val eventFormState = viewModel.eventFormState.collectAsState()
    val validationState = viewModel.validationState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.new_event_title), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedFormTextField(
            eventFormState.value.title,
            stringResource(R.string.title_label),
            { viewModel.onTitleChange(it) },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = eventFormState.value.titleError != null,
            errorMessage = eventFormState.value.titleError
        )

        OutlinedFormTextField(
            eventFormState.value.description,
            stringResource(R.string.description_label),
            { viewModel.onDescriptionChange(it) },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = eventFormState.value.descriptionError != null,
            errorMessage = eventFormState.value.descriptionError
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateTimePicker(
            dateValue = eventFormState.value.startDate,
            timeValue = eventFormState.value.startTime,
            dateLabel = stringResource(R.string.start_date_label),
            timeLabel = stringResource(R.string.start_time_label),
            isError = eventFormState.value.datesError != null,
            onDateSelected = { viewModel.onStartDateChange(it) },
            onTimeSelected = { viewModel.onStartTimeChange(it) }
        )

        DateTimePicker(
            dateValue = eventFormState.value.endDate,
            timeValue = eventFormState.value.endTime,
            dateLabel = stringResource(R.string.end_date_label),
            timeLabel = stringResource(R.string.end_time_label),
            isError = eventFormState.value.datesError != null,
            onDateSelected = { viewModel.onEndDateChange(it) },
            onTimeSelected = { viewModel.onEndTimeChange(it) }
        )


        if (eventFormState.value.datesError != null) {
            Text(eventFormState.value.datesError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedFormTextField(
            eventFormState.value.location ?: "",
            stringResource(R.string.location_label),
            { viewModel.onLocationChange(it) },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = false,
            errorMessage = null
        )

        DropDownMenu(
            calendar.teams,
            eventFormState.value.assigneeError != null
        ) { viewModel.onAssigneeChange(it) }
        if (eventFormState.value.assigneeError != null) {
            Text(eventFormState.value.assigneeError!!, color = Color.Red, fontSize = 12.sp)
        }

        Row (
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.dismiss_dialog_text))
            }
            TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    coroutineScope.launch {
                        viewModel.addEvent()
                        if (validationState.value) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(teams: List<String>, isError: Boolean, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            label = { Text(text = stringResource(R.string.assignee_label)) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
        )

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
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun EventContent(modifier: Modifier, eventList: List<Event>, onEventClicked: (String) -> Unit) {
    Column (modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.events_title),
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
                    text = eventList.size.toString(),
                    fontSize = 20.sp
                )
            }
        }
        if (eventList.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.no_events_text),
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventList) { item ->
                    EventItem(
                        item.uid,
                        item.title,
                        item.description,
                        item.assignee,
                        "${getTimeFromTimeStamp(item.startDateTime)} - ${getTimeFromTimeStamp(item.endDateTime)}",
                        onEventClicked
                    )
                }
            }
        }
    }
}

@Composable
fun EventItem(uid: String, name: String, description: String, team: String, timeSlot: String, onEventClicked: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clickable { onEventClicked(uid) }
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
            Row (
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = team,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = timeSlot,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun DateTimePicker(
    dateValue: String,
    timeValue: String,
    dateLabel: String,
    timeLabel: String,
    isError: Boolean,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        DatePickerField(
            value = dateValue,
            label = dateLabel,
            isError = isError,
            onDateSelected = { date ->

                onDateSelected(date)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        TimePickerField(
            value = timeValue,
            label = timeLabel,
            isError = isError,
            onTimeSelected = { time ->
                onTimeSelected(time)
            }
        )
    }
}