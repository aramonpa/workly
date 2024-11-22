package com.aramonp.workly.presentation.screen.calendar

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.screen.home.CalendarItem
import com.aramonp.workly.presentation.screen.home.HomeViewModel
import com.aramonp.workly.presentation.screen.home.RegisterField
import com.aramonp.workly.util.combineDateAndTime
import com.aramonp.workly.util.convertMillisToDate
import com.aramonp.workly.util.getTimeFromTimeStamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(id: String, viewModel: CalendarViewModel, navController: NavHostController) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val calendarState = viewModel.calendarState.collectAsState()

    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            viewModel.fetchCalendar(id)
        }
    }

    Scaffold(
        topBar = {
            CalendarTopBar(id, navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add calendar")
            }
        }
    ) {
        when (val state = calendarState.value) {
            is HomeState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    CalendarContent(viewModel)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    EventContent(
                        Modifier
                            .padding(it)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        state.data.events
                    )

                    if (showDialog) {
                        ShowDialogSurface(
                            viewModel,
                            state.data,
                            onDismiss = { showDialog = false }
                        )
                    }
                }
            }
            is HomeState.Error -> {
                Text(state.message)
            }
            else -> Unit
        }

    }
}

@Composable
fun CalendarTopBar(id: String, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        IconButton (
            onClick = {

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
                    text = { Text("Ajustes") },
                    onClick = { navController.navigate(Route.CalendarSettingsScreen.createRoute(id)) }
                )
            }
        }
    }
}

@Composable
fun CalendarContent(viewModel: CalendarViewModel) {
    val currentMonth = viewModel.currentMonth
    val selectedDate = viewModel.selectedDate
    val daysInMonth = viewModel.getDaysInMonth()

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
            DayItem(day = day, selected = selectedDate == day) {
                viewModel.selectedDate = day  // Cambiar la fecha seleccionada desde el ViewModel
            }
        }
    }

    // Mostrar la fecha seleccionada
    /*selectedDate?.let { text ->
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Seleccionaste: $text")
    }*/
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
fun DayItem(day: LocalDate, selected: Boolean, onClick: () -> Unit) {
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
            color = if (selected) Color.White else Color.Black
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
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var selectedStartDate by rememberSaveable { mutableStateOf("") }
    var selectedStartTime by rememberSaveable { mutableStateOf("") }
    var selectedEndDate by rememberSaveable { mutableStateOf("") }
    var selectedEndTime by rememberSaveable { mutableStateOf("") }
    var selectedTeam by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nuevo evento", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            name,
            "Título",
            { name = it },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        RegisterField(
            description,
            "Descripción",
            { description = it },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        DatePickerField(
            value = selectedStartDate,
            label = "Fecha de inicio",
            onDateSelected = { date ->
                selectedStartDate = date
            }
        )

        TimePickerField(
            value = selectedStartTime,
            label = "Hora de inicio",
            onTimeSelected = { time ->
                selectedStartTime = time
            }
        )

        DatePickerField(
            value = selectedEndDate,
            label = "Fecha de inicio",
            onDateSelected = { date ->
                selectedEndDate = date
            }
        )

        TimePickerField(
            value = selectedEndTime,
            label = "Hora de inicio",
            onTimeSelected = { time ->
                selectedEndTime = time
            }
        )

        RegisterField(
            description,
            "Localización",
            { location = it },
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        DropDownMenu(calendar.teams) { selectedTeam = it }

        Row {
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
                        viewModel.addEvent(
                            name,
                            description,
                            location,
                            combineDateAndTime(selectedStartDate, selectedStartTime),
                            combineDateAndTime(selectedEndDate, selectedEndTime),
                            selectedTeam
                        )

                    }
                }
            ) {
                Text("Crear")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    label: String,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Campo de fecha
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(value) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showDatePicker = true
                    }
                }
            }
    )

    // Mostrar el DatePicker si `showDatePicker` es true
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { convertMillisToDate(it) }
                        ?.let { onDateSelected(it) }
                    showDatePicker = false
                }) {
                    Text("Seleccionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    value: String,
    label: String,
    onTimeSelected: (String) -> Unit
) {
    val timePickerState = rememberTimePickerState()
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    // Campo de fecha
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(value) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showTimePicker = true
                    }
                }
            }
    )

    // Mostrar el DatePicker si `showDatePicker` es true
    if (showTimePicker) {
        Dialog(
            onDismissRequest = { showTimePicker = false },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 3.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timePickerState
                    )
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancelar")
                        }
                        TextButton(onClick = {
                            onTimeSelected(String.format(
                                Locale.getDefault(),
                                "%02d:%02d", timePickerState.hour, timePickerState.minute)
                            )
                            showTimePicker = false
                        }) {
                            Text("Seleccionar")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(teams: List<String>, onItemSelected: (String) -> Unit) {
    val coffeeDrinks = arrayOf("Americano", "Cappuccino", "Espresso", "Latte", "Mocha")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            label = { Text(text = "Participantes") },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
fun EventContent(modifier: Modifier, eventList: List<Event>) {
    Column (modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Eventos",
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
                text = "No tienes ningún calendario aún.",
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventList) { item ->
                    EventItem(
                        item.title,
                        item.description,
                        item.participant,
                        "${getTimeFromTimeStamp(item.startDate)} - ${getTimeFromTimeStamp(item.endDate)}"
                        )
                }
            }
        }
    }
}

@Composable
fun EventItem(name: String, description: String, team: String, timeSlot: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clickable {  }
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
            Row {
                Text(
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
fun CalendarMenu(
    expanded: Boolean
) {
}