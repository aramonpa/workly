package com.aramonp.workly.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
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

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _descriptionError = MutableStateFlow<String?>(null)
    val descriptionError: StateFlow<String?> = _descriptionError

    private val _teamError = MutableStateFlow<String?>(null)
    val teamError: StateFlow<String?> = _teamError

    private val _calendarId = MutableStateFlow("")

    fun onNameChange(name: String) {
        onCalendarFieldChange { calendar -> calendar.copy(name = name) }
    }

    fun onDescriptionChange(description: String) {
        onCalendarFieldChange { calendar -> calendar.copy(description = description) }
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

    suspend fun fetchCalendar(calendarId: String) {
        _calendarId.value = calendarId
        getCalendarInfo(calendarId)
            .onSuccess { calendar ->
                _settingsState.value = UiState.Success(calendar!!)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    private suspend fun getCalendarInfo(calendarId: String): Result<Calendar?> {
        return firestoreRepository.getCalendar(calendarId)
    }

    suspend fun updateCalendarInfo() {
        val state = (settingsState.value as UiState.Success<Calendar>)

        if (!validateFields(state.data)) {
            return
        }

        viewModelScope.launch {
            firestoreRepository.updateCalendar(
                _calendarId.value,
                mapOf(
                    "name" to state.data.name,
                    "description" to state.data.description,
                    "updatedAt" to Timestamp.now()
                )
            )
        }
    }

    suspend fun addTeam(teamName: String) {
        if (!validateTeam(teamName)) {
            return
        }

        firestoreRepository.addTeamToCalendar(
            _calendarId.value,
            teamName
        )
        onTeamChange(teamName, true)
    }

    suspend fun deleteTeam(teamName: String) {
        viewModelScope.launch {
            firestoreRepository.deleteTeamToCalendar(
                _calendarId.value,
                teamName
            )
            onTeamChange(teamName, false)
        }
    }

    private fun validateFields(calendar: Calendar): Boolean {
        val nameValidation = validateField(calendar.name)
        val descriptionValidation = validateField(calendar.description)

        _nameError.value = nameValidation.errorMessage
        _descriptionError.value = descriptionValidation.errorMessage

        // Si hay errores, no continuar
        return nameValidation.success && descriptionValidation.success
    }

    private fun validateTeam(team: String): Boolean {
        val teamValidation = validateField(team)
        _nameError.value = teamValidation.errorMessage

        return teamValidation.success
    }
}