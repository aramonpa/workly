package com.aramonp.workly.presentation.screen.calendar.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.UiState
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _eventState = MutableStateFlow<UiState<Event>>(UiState.Loading)
    val eventState: StateFlow<UiState<Event>> = _eventState

    private val _teams = MutableStateFlow<List<String>>(emptyList())
    val teams: StateFlow<List<String>> = _teams

    private val _calendarId = MutableStateFlow("")
    private val _eventId = MutableStateFlow("")

    fun onTitleChange(name: String) {
        onEventFieldChange { event -> event.copy(title = name) }
    }

    fun onDescriptionChange(description: String) {
        onEventFieldChange { event -> event.copy(description = description) }
    }

    fun onStartDateChange(startDate: Timestamp) {
        onEventFieldChange { event -> event.copy(startDate = startDate) }
    }

    fun onEndDateChange(endDate: Timestamp) {
        onEventFieldChange { event -> event.copy(endDate = endDate) }
    }

    fun onLocationChange(location: String) {
        onEventFieldChange { event -> event.copy(location = location) }
    }

    fun onAssigneeChange(assignee: String) {
        onEventFieldChange { event -> event.copy(assignee = assignee) }
    }

    fun fetchEvent(calendarId: String, eventId: String) {
        _calendarId.value = calendarId
        _eventId.value = eventId

        viewModelScope.launch {
            getEventInfo(calendarId, eventId)
                .onSuccess { event ->
                    _eventState.value = UiState.Success(event!!)
                }
                .onFailure { error ->
                    UiState.Error(error.message.orEmpty())
                }
        }
    }

    fun fetchTeams(calendarId: String) {
        viewModelScope.launch {
            getTeams(calendarId)
                .onSuccess { teams ->
                    _teams.value = teams
                }
                .onFailure { error ->
                    UiState.Error(error.message.orEmpty())
                }
        }
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

    suspend fun updateEventInfo() {
        val state = (_eventState.value as UiState.Success<Event>)
        viewModelScope.launch {
            firestoreRepository.updateEvent(
                _calendarId.value,
                _eventId.value,
                mapOf(
                    "title" to state.data.title,
                    "description" to state.data.description,
                    "startDate" to state.data.startDate,
                    "endDate" to state.data.endDate,
                    "location" to state.data.location,
                    "assignee" to state.data.assignee,
                    "updatedAt" to Timestamp.now()
                )
            )
        }
    }

    suspend fun deleteEvent() {
        viewModelScope.launch {
            firestoreRepository.deleteEvent(_calendarId.value, _eventId.value)
        }
    }
}