package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.use_case.ValidateField
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    //TODO: Replace impls with interfaces ex: AuthRepositoryImpl -> AuthRepository
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField
) : ViewModel() {
    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState

    private val _calendarListState = MutableStateFlow<UiState<List<Calendar>>>(UiState.Loading)
    val calendarListState: StateFlow<UiState<List<Calendar>>> = _calendarListState

    private val _calendarList = MutableStateFlow<List<Calendar>>(emptyList())

    private val _calendarName = MutableStateFlow("")
    val calendarName: StateFlow<String> = _calendarName

    private val _calendarNameError = MutableStateFlow<String?>(null)
    val calendarNameError: StateFlow<String?> = _calendarNameError

    private val _calendarDescription = MutableStateFlow("")
    val calendarDescription: StateFlow<String> = _calendarDescription

    private val _calendarDescriptionError = MutableStateFlow<String?>(null)
    val calendarDescriptionError: StateFlow<String?> = _calendarDescriptionError

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _userState.value = UiState.Loading
        viewModelScope.launch {
            val authResult = authRepository.getCurrentUser()
            authResult.fold(
                onSuccess = { user -> handleUser(user) },
                onFailure = { error ->
                    UiState.Error(error.message.orEmpty())
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
                    UiState.Error(error.message.orEmpty())
                }
        }
    }

    private suspend fun fetchUserCalendars(uid: String, user: User) {
        val calendarResult = getCalendarsByUser(uid)
        calendarResult
            .onSuccess { calendars ->
                _calendarList.value = calendars
                _userState.value = UiState.Success(user)
                _calendarListState.value = UiState.Success(calendars)
            }
            .onFailure { error ->
                _userState.value = UiState.Error(error.message.orEmpty())
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
        //_calendarListState.value = UiState.Loading

        if (!validateFields()) {
            return
        }

        val currentUser = authRepository.getCurrentUser().getOrNull()
        val uid = currentUser?.uid!!

        val calendar = Calendar(
            name = _calendarName.value,
            description = _calendarDescription.value,
            ownerId = uid,
            createdAt = Timestamp.now(),
            members = listOf(uid)
        )

        firestoreRepository.createCalendar(calendar)
            .onSuccess {
                calendar.uid = it
                _calendarList.value += calendar
                _calendarListState.value = UiState.Success(_calendarList.value)
            }
            .onFailure { _calendarListState.value = UiState.Error(it.message.orEmpty()) }
    }

    fun onNameChange(name: String) {
        _calendarName.value = name
    }

    fun onDescriptionChange(description: String) {
        _calendarDescription.value = description
    }

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_calendarName.value)
        val descriptionValidation = validateField(_calendarDescription.value)

        _calendarNameError.value = nameValidation.errorMessage
        _calendarDescriptionError.value = descriptionValidation.errorMessage

        return nameValidation.success && descriptionValidation.success
    }

}