package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _userState = MutableStateFlow<HomeState<User>>(HomeState.Loading)
    val userState: StateFlow<HomeState<User>> = _userState

    private val _calendarListState = MutableStateFlow<HomeState<List<Calendar>>>(HomeState.Loading)
    val calendarListState: StateFlow<HomeState<List<Calendar>>> = _calendarListState

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())

    private val _calendarName = MutableStateFlow("")
    val calendarName: StateFlow<String> = _calendarName

    private val _calendarDescription = MutableStateFlow("")
    val calendarDescription: StateFlow<String> = _calendarDescription

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _userState.value = HomeState.Loading
        viewModelScope.launch {
            val authResult = authRepository.getCurrentUser()
            authResult.fold(
                onSuccess = { user -> handleUser(user) },
                onFailure = { error ->
                    HomeState.Error(error.message.orEmpty())
                }
            )
        }
    }

    private suspend fun handleUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.uid?.let { uid ->
            getUserInfo(uid)
                .onSuccess { user ->
                    fetchUserCalendars(uid, user!!)
                }
                .onFailure { error ->
                    HomeState.Error(error.message.orEmpty())
                }
        }
    }

    private suspend fun fetchUserCalendars(uid: String, user: User) {
        val calendarResult = getCalendarsByUser(uid)
        calendarResult
            .onSuccess { calendars ->
                _calendarList.value = calendars
                _userState.value = HomeState.Success(user)
                _calendarListState.value = HomeState.Success(calendars)
            }
            .onFailure { error ->
                _userState.value = HomeState.Error(error.message.orEmpty())
            }
    }

    // TODO: Review if it's better to use simple Types
    private suspend fun getUserInfo(uid: String): Result<User?> {
        return firestoreRepository.getUser(uid)
    }

    private suspend fun getCalendarsByUser(uid: String): Result<List<Calendar>> {
        return firestoreRepository.getAllCalendarsByUser(uid)
    }

    suspend fun createCalendar() {
        //_calendarListState.value = HomeState.Loading

        if (_calendarName.value.isNotEmpty()) {
            val currentUser = authRepository.getCurrentUser().getOrNull()
            val uid = currentUser?.uid!!

            val calendar = Calendar(
                name = _calendarName.value,
                description = _calendarDescription.value,
                ownerId = uid,
                createdAt = Timestamp.now(),
                members = listOf(uid)
            )

            viewModelScope.launch {
                firestoreRepository.createCalendar(calendar)
                    .onSuccess {
                        calendar.uid = it
                        _calendarList.value += calendar
                        _calendarListState.value = HomeState.Success(_calendarList.value)
                    }
                    .onFailure { _calendarListState.value = HomeState.Error(it.message.orEmpty()) }
            }
        }
    }

    fun onNameChange(name: String) {
        _calendarName.value = name
    }

    fun onDescriptionChange(description: String) {
        _calendarDescription.value = description
    }
}