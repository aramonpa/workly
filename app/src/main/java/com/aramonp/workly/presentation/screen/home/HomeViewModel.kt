package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.HomeState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
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
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())
    val calendarList: StateFlow<List<Calendar>> = _calendarList

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _homeState.value = HomeState.Loading
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
                    _user.value = user
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
                _homeState.value = HomeState.Success(user, calendars)
            }
            .onFailure { error ->
                _homeState.value = HomeState.Error(error.message.orEmpty())
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
        _homeState.value = HomeState.Loading

        if (_name.value.isNotEmpty()) {
            val currentUser = authRepository.getCurrentUser().getOrNull()
            val uid = currentUser?.uid!!

            val calendar = Calendar(
                name = _name.value,
                description = _description.value,
                ownerId = uid,
                createdAt = Timestamp.now(),
                members = listOf(uid)
            )

            viewModelScope.launch {
                firestoreRepository.createCalendar(calendar)
                    .onSuccess {
                        _calendarList.value += calendar
                        _homeState.value = HomeState.Success(_user.value!!, calendar)
                    }
                    .onFailure { _homeState.value = HomeState.Error(it.message.orEmpty()) }
            }
        }
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }


}