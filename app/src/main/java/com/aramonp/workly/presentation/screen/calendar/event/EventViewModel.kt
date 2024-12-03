package com.aramonp.workly.presentation.screen.calendar.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.EventFormState
import com.aramonp.workly.domain.model.SettingsFormState
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.use_case.ValidateDates
import com.aramonp.workly.domain.use_case.ValidateField
import com.aramonp.workly.util.combineDateAndTime
import com.aramonp.workly.util.convertLocalDateToTimestamp
import com.aramonp.workly.util.convertToTimestamp
import com.aramonp.workly.util.getDateFromTimeStamp
import com.aramonp.workly.util.getTimeFromTimeStamp
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Time
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField,
    private val validateDates: ValidateDates,
) : ViewModel() {
    private val _eventState = MutableStateFlow<UiState<Event>>(UiState.Loading)
    val eventState: StateFlow<UiState<Event>> = _eventState

    private val _teams = MutableStateFlow<List<String>>(emptyList())
    val teams: StateFlow<List<String>> = _teams

    private val _eventFormState = MutableStateFlow(EventFormState())
    val eventFormState: StateFlow<EventFormState> = _eventFormState

    private val _validationState = MutableStateFlow(false)
    val validationState: StateFlow<Boolean> = _validationState

    private val _calendarId = MutableStateFlow("")
    private val _eventId = MutableStateFlow("")

    suspend fun fetchEvent(calendarId: String, eventId: String) {
        _calendarId.value = calendarId
        _eventId.value = eventId

        getEventInfo(calendarId, eventId)
            .onSuccess { event ->
                fillFormState(event!!)
                _eventState.value = UiState.Success(event)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    suspend fun fetchTeams(calendarId: String) {
        getTeams(calendarId)
            .onSuccess { teams ->
                _teams.value = teams
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    suspend fun updateEventInfo() {
        if (validateFields()) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }

        firestoreRepository.updateEvent(
            _calendarId.value,
            _eventId.value,
            mapOf(
                "title" to _eventFormState.value.title,
                "description" to _eventFormState.value.description,
                "startDateTime" to _eventFormState.value.startDateTime,
                "endDateTime" to _eventFormState.value.endDateTime,
                "location" to _eventFormState.value.location,
                "assignee" to _eventFormState.value.assignee,
                "updatedAt" to Timestamp.now()
            )
        )
        onEventFieldChange { event -> event.copy(title = _eventFormState.value.title) }
        onEventFieldChange { event -> event.copy(description = _eventFormState.value.description) }
        onEventFieldChange { event -> event.copy(startDateTime = _eventFormState.value.startDateTime) }
        onEventFieldChange { event -> event.copy(endDateTime = _eventFormState.value.endDateTime) }
        onEventFieldChange { event -> event.copy(location = _eventFormState.value.location) }
        onEventFieldChange { event -> event.copy(assignee = _eventFormState.value.assignee) }
    }

    suspend fun deleteEvent() {
        firestoreRepository.deleteEvent(_calendarId.value, _eventId.value)
    }

    private fun fillFormState(event: Event) {
        _eventFormState.value = _eventFormState.value.copy(title = event.title)
        _eventFormState.value = _eventFormState.value.copy(description = event.description)
        _eventFormState.value = _eventFormState.value.copy(startDateTime = event.startDateTime)
        _eventFormState.value = _eventFormState.value.copy(endDateTime = event.endDateTime)
        _eventFormState.value = _eventFormState.value.copy(location = event.location)
        _eventFormState.value = _eventFormState.value.copy(assignee = event.assignee)
    }

    private suspend fun getTeams(calendarId: String): Result<List<String>> {
        return firestoreRepository.getTeams(calendarId)
    }

    private suspend fun getEventInfo(calendarId: String, eventId: String): Result<Event?> {
        return firestoreRepository.getEventInfo(calendarId, eventId)
    }

    private fun onEventFieldChange(fieldUpdater: (Event) -> Event) {
        _eventState.value = _eventState.value.let {
            when (it) {
                is UiState.Success -> {
                    val updatedEvent = fieldUpdater(it.data)
                    UiState.Success(updatedEvent)
                }
                else -> it
            }
        }
    }

    fun onTitleChange(title: String) {
        _eventFormState.value = _eventFormState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _eventFormState.value = _eventFormState.value.copy(description = description)
    }

    fun onStartDateTimeChange(startDateTime: Timestamp) {
        _eventFormState.value = _eventFormState.value.copy(startDateTime = startDateTime)
    }

    fun onEndDateTimeChange(endDateTime: Timestamp) {
        _eventFormState.value = _eventFormState.value.copy(endDateTime = endDateTime)
    }

    fun onLocationChange(location: String) {
        _eventFormState.value = _eventFormState.value.copy(location = location)
    }

    fun onAssigneeChange(assignee: String) {
        _eventFormState.value = _eventFormState.value.copy(assignee = assignee)
    }

    private fun validateFields(): Boolean {
        val titleValidation = validateField(_eventFormState.value.title)
        val descriptionValidation = validateField(_eventFormState.value.description)
        val datesValidation = validateDates(_eventFormState.value.startDateTime, _eventFormState.value.endDateTime)
        val assigneeValidation = validateField(_eventFormState.value.assignee)

        _eventFormState.value = _eventFormState.value.copy(
            titleError = titleValidation.errorMessage,
            descriptionError = descriptionValidation.errorMessage,
            datesError = datesValidation.errorMessage,
            assigneeError = assigneeValidation.errorMessage
        )

        return titleValidation.success && descriptionValidation.success && datesValidation.success &&
                assigneeValidation.success
    }
}