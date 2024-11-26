package com.aramonp.workly.presentation.screen.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.util.convertToTimestamp
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    var selectedDate by mutableStateOf<LocalDate>(LocalDate.now())
    var currentMonth: YearMonth by mutableStateOf(YearMonth.now())

    private val _calendarState = MutableStateFlow<UiState<Calendar>>(UiState.Loading)
    val calendarState: StateFlow<UiState<Calendar>> = _calendarState

    private val _eventsState = MutableStateFlow<UiState<List<Event>?>>(UiState.Loading)
    val eventsState: StateFlow<UiState<List<Event>?>> = _eventsState

    private val _calendarId = MutableStateFlow("")

    // Formateador para el nombre del mes en español
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))

    fun getDaysInMonth(): List<LocalDate> {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val daysInMonth = mutableListOf<LocalDate>()

        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // Calcular cuántos días vacíos necesitamos antes del primer día del mes para que la semana comience en lunes
        val daysBeforeFirstDay = if (firstDayOfWeek == 1) 0 else firstDayOfWeek - 1

        // Llenar los días vacíos hasta el primer día de la semana
        var day = firstDayOfMonth.minusDays(daysBeforeFirstDay.toLong())
        while (day.isBefore(firstDayOfMonth)) {
            daysInMonth.add(day)
            day = day.plusDays(1)
        }

        // Llenar los días del mes
        for (i in 0 until lastDayOfMonth.dayOfMonth) {
            daysInMonth.add(day)
            day = day.plusDays(1)
        }

        // Calcular los días restantes del siguiente mes para completar la cuadrícula
        val totalDaysInMonth = daysInMonth.size
        val totalWeeks = (totalDaysInMonth + 6) / 7 // Número de filas completas de la cuadrícula

        // Si el mes no llena 6 filas, agregamos los días del siguiente mes para completar la cuadrícula
        if (totalDaysInMonth % 7 != 0) {
            // Calcular el primer día del siguiente mes
            val nextMonthFirstDay = currentMonth.plusMonths(1).atDay(1)
            var nextMonthDay = nextMonthFirstDay

            // Llenar los días faltantes con días del siguiente mes
            while (daysInMonth.size < totalWeeks * 7) {
                daysInMonth.add(nextMonthDay)
                nextMonthDay = nextMonthDay.plusDays(1)
            }
        }

        return daysInMonth
    }

    fun changeMonth(newMonth: YearMonth) {
        currentMonth = newMonth
    }

    suspend fun changeSelectedDay(date: LocalDate) {
        selectedDate = date
        fetchEvents()
    }

    fun getCurrentMonthName(): String {
        // Obtiene el nombre del mes en español
        return currentMonth.format(monthFormatter).uppercase()
    }

    suspend fun fetchCalendar(calendarId: String) {
        _calendarId.value = calendarId
        getCalendarInfo(calendarId)
            .onSuccess { calendar ->

                _calendarState.value = UiState.Success(calendar!!)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    suspend fun fetchEvents() {
        getEventsInfo(_calendarId.value, selectedDate)
            .onSuccess { events ->
                _eventsState.value = UiState.Success(events)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }


    private fun onCalendarFieldChange(fieldUpdater: (Calendar) -> Calendar) {
        _calendarState.value = _calendarState.value.let {
            when (it) {
                is UiState.Success -> {
                    val updatedCalendar = fieldUpdater(it.data)
                    UiState.Success(updatedCalendar)
                }
                else -> it
            }
        }
    }

    suspend fun addEvent(
        name: String,
        description: String,
        location: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        team: String
    ) {
        val event = Event(
            title = name,
            description = description,
            startDate = convertToTimestamp(startDate),
            endDate = convertToTimestamp(endDate),
            createdAt = Timestamp.now(),
            location = location,
            assignee = team
        )
        viewModelScope.launch {
            firestoreRepository.addEvent(
                _calendarId.value,
                event
            )
        }
        //val currentEvents = (_eventsState.value as? UiState.Success)?.data.orEmpty()
        //_eventsState.value = UiState.Success(currentEvents + event)
        fetchEvents()
    }

    private suspend fun getCalendarInfo(calendarId: String): Result<Calendar?> {
        return firestoreRepository.getCalendar(calendarId)
    }

    private suspend fun getEventsInfo(calendarId: String, date: LocalDate): Result<List<Event>?> {
        return firestoreRepository.getAllEventsByDay(calendarId, date)
    }

    suspend fun deleteCalendar() {
        viewModelScope.launch {
            firestoreRepository.deleteCalendar(_calendarId.value)
        }
    }
}