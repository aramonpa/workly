package com.aramonp.workly.presentation.screen.calendar
/*
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(id: String) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Obtener los días del mes actual
    val daysInMonth = getDaysInMonth(currentMonth)

    Scaffold(
        topBar = {
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
                IconButton (
                    onClick = {

                    }
                ) {
                    Image(imageVector = Icons.Default.MoreVert, contentDescription = "Atrás")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            // Fila con las flechas para cambiar el mes
            MonthNavigation(currentMonth) { newMonth ->
                currentMonth = newMonth
            }

            // Título del mes
            /*Text(
                text = currentMonth.month.name + " " + currentMonth.year,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )*/

            // Mostrar los días de la semana (Lun, Mar, Mié, ...)
            WeekdaysHeader()

            // Mostrar los días del mes
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(daysInMonth) { day ->
                    DayItem(day = day, selected = selectedDate == day) {
                        selectedDate = day
                    }
                }
            }

            // Mostrar la fecha seleccionada
            /*selectedDate?.let { text ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Seleccionaste: $text")
            }*/
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }


}

@Composable
fun MonthNavigation(currentMonth: YearMonth, onMonthChanged: (YearMonth) -> Unit) {
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
            text = currentMonth.month.name + " " + currentMonth.year,
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

fun getDaysInMonth(month: YearMonth): List<LocalDate> {
    val firstDayOfMonth = month.atDay(1)
    val lastDayOfMonth = month.atEndOfMonth()
    val daysInMonth = mutableListOf<LocalDate>()

    // Llenar los días vacíos hasta el primer día de la semana
    var day = firstDayOfMonth
    while (day.isBefore(firstDayOfMonth.withDayOfMonth(1))) {
        daysInMonth.add(day)
        day = day.plusDays(1)
    }

    // Llenar los días del mes
    for (i in 0 until lastDayOfMonth.dayOfMonth) {
        daysInMonth.add(day)
        day = day.plusDays(1)
    }

    return daysInMonth
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalendarScreen("")
}

 */