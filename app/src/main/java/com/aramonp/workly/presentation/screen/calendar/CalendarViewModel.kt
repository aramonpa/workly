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
import com.aramonp.workly.domain.use_case.ValidateDates
import com.aramonp.workly.domain.use_case.ValidateField
import com.aramonp.workly.domain.use_case.ValidatePassword
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
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField,
    private val validateDates: ValidateDates
) : ViewModel() {
    var selectedDate by mutableStateOf<LocalDate>(LocalDate.now())
    var currentMonth: YearMonth by mutableStateOf(YearMonth.now())

    private val _calendarState = MutableStateFlow<UiState<Calendar>>(UiState.Loading)
    val calendarState: StateFlow<UiState<Calendar>> = _calendarState

    private val _eventsState = MutableStateFlow<UiState<List<Event>?>>(UiState.Loading)
    val eventsState: StateFlow<UiState<List<Event>?>> = _eventsState

    /*
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors
    val nameResult = validateEventName(name)
        if (!nameResult.successful) {
            errors["name"] = nameResult.errorMessage.orEmpty()
        }
     */

    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError
    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError: StateFlow<String?> = _descriptionError
    private val _datesError = MutableStateFlow<String?>(null)
    val datesError: StateFlow<String?> = _datesError
    private val _assigneeError = MutableStateFlow<String?>(null)
    val assigneeError: StateFlow<String?> = _assigneeError

    private val _calendarId = MutableStateFlow("")

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

    private suspend fun getCalendarInfo(calendarId: String): Result<Calendar?> {
        return firestoreRepository.getCalendar(calendarId)
    }

    private suspend fun getEventsInfo(calendarId: String, date: LocalDate): Result<List<Event>?> {
        return firestoreRepository.getAllEventsByDay(calendarId, date)
    }

    suspend fun addEvent(
        name: String,
        description: String,
        location: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        assignee: String
    ) {
        val event = Event(
            title = name,
            description = description,
            startDate = convertToTimestamp(startDate),
            endDate = convertToTimestamp(endDate),
            createdAt = Timestamp.now(),
            location = location,
            assignee = assignee
        )

        if (!validateFields(event)) {
            return
        }

        firestoreRepository.addEvent(
            _calendarId.value,
            event
        )

        //val currentEvents = (_eventsState.value as? UiState.Success)?.data.orEmpty()
        //_eventsState.value = UiState.Success(currentEvents + event)
        fetchEvents()
    }

    suspend fun deleteCalendar() {
        viewModelScope.launch {
            firestoreRepository.deleteCalendar(_calendarId.value)
        }
    }

    private fun validateFields(event: Event): Boolean {
        val titleValidation = validateField(event.title)
        val descriptionValidation = validateField(event.description)
        val datesValidation = validateDates(event.startDate, event.endDate)
        val assigneeValidation = validateField(event.assignee)

        _titleError.value = titleValidation.errorMessage
        _descriptionError.value = descriptionValidation.errorMessage
        _datesError.value = datesValidation.errorMessage
        _assigneeError.value = assigneeValidation.errorMessage

        // Si hay errores, no continuar
        return titleValidation.success && descriptionValidation.success && datesValidation.success &&
                assigneeValidation.success
    }
}