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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.model.TopLevelRoute
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.navigation.Route
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onNavigateToCalendar: (String) -> Unit, viewModel: HomeViewModel) {
    val userState = viewModel.userState.collectAsState()
    val calendarListState = viewModel.calendarListState.collectAsState()

    when (userState.value) {
        is HomeState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is HomeState.Success -> {
            HomeContent(
                (userState.value as HomeState.Success<User>).data,
                calendarListState.value,
                viewModel,
                onNavigateToCalendar
            )
        }
        is HomeState.Error -> {
            Text("Error al cargar.", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HomeContent(
    user: User,
    calendarListState: HomeState<List<Calendar>>,
    viewModel: HomeViewModel,
    navigation: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val calendarName: String by viewModel.calendarName.collectAsState()
    val calendarDescription: String by viewModel.calendarDescription.collectAsState()

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    "¡Hola!",
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
            BottomNavigationBar()

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add calendar")
            }
        }
    ) {
        if (showDialog) {
            ShowDialogSurface(viewModel, calendarName, calendarDescription, onDismiss = { showDialog = false })
        }

        when (calendarListState) {
            is HomeState.Loading -> {
                CircularProgressIndicator()
            }
            is HomeState.Success  -> {
                CalendarList(
                    Modifier
                        .padding(it)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    calendarListState.data.size,
                    calendarListState.data,
                    navigation
                )
            }
            is HomeState.Error -> {
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
    navigation: (String) -> Unit) {
    Column (modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Mis calendarios",
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
                text = "No tienes ningún calendario aún.",
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(calendarList) { item ->
                    CalendarItem(item.name, item.description, item.uid, navigation)
                }
            }
        }
    }
}

@Composable
fun CalendarItem(name: String, description: String, id: String, navigation: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .clickable { navigation(id) }
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
fun BottomNavigationBar() {
    val topLevelRoutes = listOf(
        TopLevelRoute("Home", Route.HomeScreen, Icons.Default.Home),
        TopLevelRoute("Perfil", Route.HomeScreen, Icons.Default.AccountCircle),
    )

    val selected = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar (
    ) {
        topLevelRoutes.forEachIndexed { index, topLevelRoute ->
            NavigationBarItem(
                selected = selected.intValue == index,
                onClick = {},
                icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.name) },
                label = { Text(topLevelRoute.name) }
            )
        }
    }
}

@Composable
fun ShowDialogSurface(viewModel: HomeViewModel, name: String, description: String, onDismiss: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nuevo calendario", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                RegisterField(
                    name,
                    "Nombre",
                    { value -> viewModel.onNameChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                RegisterField(
                    description,
                    "Descripción",
                    { value -> viewModel.onDescriptionChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text)
                )

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
                                viewModel.createCalendar()
                                viewModel.onNameChange("")
                                viewModel.onDescriptionChange("")
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

@Composable
fun ShowDialogCard(viewModel: HomeViewModel, name: String, description: String, onDismiss: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nuevo calendario")
                Spacer(modifier = Modifier.height(20.dp))

                RegisterField(
                    name,
                    "Nombre",
                    { value -> viewModel.onNameChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                RegisterField(
                    description,
                    "Descripción",
                    { value -> viewModel.onDescriptionChange(value.trim()) },
                    Modifier.fillMaxWidth(),
                    KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun RegisterField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOption: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false
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
}