package com.aramonp.workly.presentation.screen.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.aramonp.workly.util.convertToTimestamp
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

    private val _calendarState = MutableStateFlow<HomeState<Calendar>>(HomeState.Loading)
    val calendarState: StateFlow<HomeState<Calendar>> = _calendarState

    private val _calendarId = MutableStateFlow("")

    // Formateador para el nombre del mes en español
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))

    fun getDaysInMonth(): List<LocalDate> {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
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

    fun changeMonth(newMonth: YearMonth) {
        currentMonth = newMonth
    }

    fun getCurrentMonthName(): String {
        // Obtiene el nombre del mes en español
        return currentMonth.format(monthFormatter).uppercase()
    }

    suspend fun fetchCalendar(calendarId: String) {
        _calendarId.value = calendarId
        getCalendarInfo(calendarId)
            .onSuccess { calendar ->

                _calendarState.value = HomeState.Success(calendar!!)
            }
            .onFailure { error ->
                HomeState.Error(error.message.orEmpty())
            }
    }

    private fun onCalendarFieldChange(fieldUpdater: (Calendar) -> Calendar) {
        _calendarState.value = _calendarState.value.let {
            when (it) {
                is HomeState.Success -> {
                    val updatedCalendar = fieldUpdater(it.data)
                    HomeState.Success(updatedCalendar)
                }
                else -> it
            }
        }
    }

    private fun onEventChange(event: Event, add: Boolean) {
        onCalendarFieldChange { calendar ->
            val updatedEvents = calendar.events.toMutableList().apply {
                if (add) {
                    // Agregar el nuevo equipo, evitando duplicados
                    if (!contains(event)) add(event)
                } else {
                    // Eliminar el equipo si existe
                    remove(event)
                }
            }
            calendar.copy(events = updatedEvents)
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
            name,
            description,
            convertToTimestamp(startDate),
            convertToTimestamp(endDate),
            location,
            team
        )
        viewModelScope.launch {
            firestoreRepository.addEvent(
                _calendarId.value,
                event
            )
            //onTeamChange(teamName, true)
        }
    }

    private suspend fun getCalendarInfo(calendarId: String): Result<Calendar?> {
        return firestoreRepository.getCalendar(calendarId)
    }
}