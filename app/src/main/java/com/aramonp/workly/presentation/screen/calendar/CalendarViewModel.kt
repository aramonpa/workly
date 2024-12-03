package com.aramonp.workly.presentation.screen.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.CalendarEventFormState
import com.aramonp.workly.domain.model.CalendarFormState
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.EventFormState
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.model.ValidationResult
import com.aramonp.workly.domain.use_case.ValidateDatesFields
import com.aramonp.workly.domain.use_case.ValidateDates
import com.aramonp.workly.domain.use_case.ValidateField
import com.aramonp.workly.util.combineDateAndTime
import com.aramonp.workly.util.convertToTimestamp
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
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

    private val _eventFormState = MutableStateFlow(CalendarEventFormState())
    val eventFormState: StateFlow<CalendarEventFormState> = _eventFormState

    private val _validationState = MutableStateFlow(false)
    val validationState: StateFlow<Boolean> = _validationState

    private val _calendarId = MutableStateFlow("")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))

    fun getDaysInMonth(): List<LocalDate> {
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val daysInMonth = mutableListOf<LocalDate>()

        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // Calculate ending days of the month before
        val daysBeforeFirstDay = if (firstDayOfWeek == 1) 0 else firstDayOfWeek - 1

        // Getting last days of the month before
        var day = firstDayOfMonth.minusDays(daysBeforeFirstDay.toLong())
        while (day.isBefore(firstDayOfMonth)) {
            daysInMonth.add(day)
            day = day.plusDays(1)
        }

        for (i in 0 until lastDayOfMonth.dayOfMonth) {
            daysInMonth.add(day)
            day = day.plusDays(1)
        }

        // Getting
        val totalDaysInMonth = daysInMonth.size
        val totalWeeks = (totalDaysInMonth + 6) / 7

        // Si el mes no llena 6 filas, agregamos los días del siguiente mes para completar la cuadrícula
        if (totalDaysInMonth % 7 != 0) {
            val nextMonthFirstDay = currentMonth.plusMonths(1).atDay(1)
            var nextMonthDay = nextMonthFirstDay

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

    suspend fun addEvent() {
        if (validateFields()) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }

        firestoreRepository.addEvent(
            _calendarId.value,
            Event(
                title = _eventFormState.value.title,
                description = _eventFormState.value.description,
                startDateTime = convertToTimestamp(combineDateAndTime(
                    _eventFormState.value.startDate,
                    _eventFormState.value.startTime
                )),
                endDateTime = convertToTimestamp(combineDateAndTime(
                    _eventFormState.value.startDate,
                    _eventFormState.value.startTime
                )),
                createdAt = Timestamp.now(),
                location = _eventFormState.value.location,
                assignee = _eventFormState.value.assignee
            )
        )
        fetchEvents()
    }

    fun onTitleChange(title: String) {
        _eventFormState.value = _eventFormState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _eventFormState.value = _eventFormState.value.copy(description = description)
    }

    fun onStartDateChange(startDate: String) {
        _eventFormState.value = _eventFormState.value.copy(startDate = startDate)
    }

    fun onStartTimeChange(startTime: String) {
        _eventFormState.value = _eventFormState.value.copy(startTime = startTime)
    }

    fun onEndDateChange(endDate: String) {
        _eventFormState.value = _eventFormState.value.copy(endDate = endDate)
    }

    fun onEndTimeChange(endTime: String) {
        _eventFormState.value = _eventFormState.value.copy(endTime = endTime)
    }

    fun onLocationChange(location: String) {
        _eventFormState.value = _eventFormState.value.copy(location = location)
    }

    fun onAssigneeChange(assignee: String) {
        _eventFormState.value = _eventFormState.value.copy(assignee = assignee)
    }

    suspend fun deleteCalendar() {
        firestoreRepository.deleteCalendar(_calendarId.value)
    }

    private fun validateFields(): Boolean {
        val titleValidation = validateField(_eventFormState.value.title)
        val descriptionValidation = validateField(_eventFormState.value.description)
        val assigneeValidation = validateField(_eventFormState.value.assignee)
        val datesFieldsValidation = ValidateDatesFields().invoke(
            _eventFormState.value.startDate,
            _eventFormState.value.startTime,
            _eventFormState.value.endDate,
            _eventFormState.value.endTime
        )

        val datesValidation = if (datesFieldsValidation.success) {
            val startDateTime = convertToTimestamp(
                combineDateAndTime(_eventFormState.value.startDate, _eventFormState.value.startTime)
            )
            val endDateTime = convertToTimestamp(
                combineDateAndTime(_eventFormState.value.endDate, _eventFormState.value.endTime)
            )
            validateDates(startDateTime, endDateTime)
        } else {
            datesFieldsValidation
        }

        _eventFormState.value = _eventFormState.value.copy(
            titleError = titleValidation.errorMessage,
            descriptionError = descriptionValidation.errorMessage,
            datesError = datesValidation.errorMessage,
            assigneeError = assigneeValidation.errorMessage
        )

        return titleValidation.success && descriptionValidation.success && datesValidation.success &&
                assigneeValidation.success
    }

    fun clearFields() {
        _eventFormState.value = CalendarEventFormState()
    }
}