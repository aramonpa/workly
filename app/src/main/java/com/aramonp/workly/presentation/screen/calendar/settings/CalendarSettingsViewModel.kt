package com.aramonp.workly.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.CalendarSettingsFormState
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.use_case.ValidateField
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarSettingsViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField
) : ViewModel() {
    private val _settingsState = MutableStateFlow<UiState<Calendar>>(UiState.Loading)
    val settingsState: StateFlow<UiState<Calendar>> = _settingsState

    private val _calendarSettingsFormState = MutableStateFlow(CalendarSettingsFormState())
    val calendarSettingsFormState: StateFlow<CalendarSettingsFormState> = _calendarSettingsFormState

    private val _validationState = MutableStateFlow(false)
    val validationState: StateFlow<Boolean> = _validationState

    private val _calendarId = MutableStateFlow("")

    suspend fun fetchCalendar(calendarId: String) {
        _calendarId.value = calendarId
        getCalendarInfo(calendarId)
            .onSuccess { calendar ->
                fillFormState(calendar!!)
                _settingsState.value = UiState.Success(calendar)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    suspend fun updateCalendarInfo() {
        if (validateFields()) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }

        firestoreRepository.updateCalendar(
            _calendarId.value,
            mapOf(
                "name" to _calendarSettingsFormState.value.name,
                "description" to _calendarSettingsFormState.value.description,
                "updatedAt" to Timestamp.now()
            )
        )
        onCalendarFieldChange { calendar -> calendar.copy(name = _calendarSettingsFormState.value.name) }
        onCalendarFieldChange { calendar -> calendar.copy(description = _calendarSettingsFormState.value.description) }
    }

    fun onNameChange(name: String) {
        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(name = name)
    }

    fun onDescriptionChange(description: String) {
        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(description = description)
    }

    private fun onTeamChange(team: String, add: Boolean) {
        onCalendarFieldChange { calendar ->
            val updatedTeams = calendar.teams.toMutableList().apply {
                if (add) {
                    if (!contains(team)) add(team)
                } else {
                    remove(team)
                }
            }
            calendar.copy(teams = updatedTeams)
        }
    }

    private fun onCalendarFieldChange(fieldUpdater: (Calendar) -> Calendar) {
        _settingsState.value = _settingsState.value.let {
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

    private fun fillFormState(calendar: Calendar) {
        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(name = calendar.name)
        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(description = calendar.description)
    }

    suspend fun addTeam(team: String) {
        if (validateTeam(team)) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }
        firestoreRepository.addTeamToCalendar(
            _calendarId.value,
            team
        )
        onTeamChange(team, true)
    }

    suspend fun deleteTeam(team: String) {
        viewModelScope.launch {
            firestoreRepository.deleteTeamToCalendar(
                _calendarId.value,
                team
            )
            onTeamChange(team, false)
        }
    }

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_calendarSettingsFormState.value.name)
        val descriptionValidation = validateField(_calendarSettingsFormState.value.description)

        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(
            nameError = nameValidation.errorMessage,
            descriptionError = descriptionValidation.errorMessage,

        )

        return nameValidation.success && descriptionValidation.success
    }

    private fun validateTeam(team: String): Boolean {
        val teamValidation = validateField(team)

        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(
            teamError = teamValidation.errorMessage
        )

        return teamValidation.success
    }

    fun clearErrors() {
        _calendarSettingsFormState.value = _calendarSettingsFormState.value.copy(
            nameError = null,
            descriptionError = null,
            teamError = null
        )
    }
}