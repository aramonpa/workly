package com.aramonp.workly.presentation.screen.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.TopLevelRoute
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.screen.login.LogInViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val firestoreState = viewModel.firestoreState.observeAsState()

    when (firestoreState.value) {
        is FirestoreState.Success -> {
            // Acceder a los datos cuando el estado es Success
            val (user, calendars) = (firestoreState.value as FirestoreState.Success<Any>).data as Pair<*, *>

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
                        if (user)
                        Text(
                            userInfo.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                },
                bottomBar = {
                    BottomNavigationBar()

                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {  }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add")
                    }
                }
            ) {
                CalendarList(
                    Modifier
                        .padding(it)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
        is FirestoreState.Error -> {
            Text("ERROR")
        }
        else -> {
            // Manejar el caso en que no esté en estado de Success (Error o Loading)
            null
        }
    }


}

@Composable
fun CalendarList(modifier: Modifier = Modifier) {
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
                    text = "3",
                    fontSize = 20.sp
                )
            }
        }
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CalendarItem("Calendario", "Calendario de alejandro")
            }
            item {
                CalendarItem("Calendario2", "Calendario de alejandro")
            }
            item {
                CalendarItem("Calendario3", "Calendario de alejandro")
            }
        }
    }
}

@Composable
fun CalendarItem(name: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
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

fun getCalendarNum(userInfo: User): Int {
    return userInfo.calendars.size
}