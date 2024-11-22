package com.aramonp.workly.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.AuthRepository
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _settingsState = MutableStateFlow<HomeState<Calendar>>(HomeState.Loading)
    val settingsState: StateFlow<HomeState<Calendar>> = _settingsState

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

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
                    // Agregar el nuevo equipo, evitando duplicados
                    if (!contains(team)) add(team)
                } else {
                    // Eliminar el equipo si existe
                    remove(team)
                }
            }
            calendar.copy(teams = updatedTeams)
        }
    }

    suspend fun fetchCalendar(calendarId: String) {
        _calendarId.value = calendarId
        getCalendarInfo(calendarId)
            .onSuccess { calendar ->
                _settingsState.value = HomeState.Success(calendar!!)
            }
            .onFailure { error ->
                HomeState.Error(error.message.orEmpty())
            }
    }

    private suspend fun getCalendarInfo(calendarId: String): Result<Calendar?> {
        return firestoreRepository.getCalendar(calendarId)
    }

    private fun onCalendarFieldChange(fieldUpdater: (Calendar) -> Calendar) {
        _settingsState.value = _settingsState.value.let {
            when (it) {
                is HomeState.Success -> {
                    val updatedCalendar = fieldUpdater(it.data)
                    HomeState.Success(updatedCalendar)
                }
                else -> it
            }
        }
    }

    suspend fun updateCalendarInfo() {
        val state = (settingsState.value as HomeState.Success<Calendar>)
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
        viewModelScope.launch {
            firestoreRepository.addTeamToCalendar(
                _calendarId.value,
                teamName
            )
            onTeamChange(teamName, true)
        }
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
}