package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.CalendarFormState
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

    private val _calendarFormState = MutableStateFlow(CalendarFormState())
    val calendarFormState: StateFlow<CalendarFormState> = _calendarFormState

    private val _validationState = MutableStateFlow(false)
    val validationState: StateFlow<Boolean> = _validationState

    suspend fun fetchUser() {
        _userState.value = UiState.Loading
            val authResult = authRepository.getCurrentUser()
            authResult
                .onSuccess { user -> handleUser(user) }
                .onFailure { error ->
                    _userState.value = UiState.Error(error.message.orEmpty())
                }
    }

    private suspend fun handleUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.uid?.let { uid ->
            getUserInfo(uid)
                .onSuccess { user ->
                    _userState.value = UiState.Success(user!!)
                }
                .onFailure { error ->
                    UiState.Error(error.message.orEmpty())
                }
        }
    }

    suspend fun fetchUserCalendars() {
        val user = _userState.value as UiState.Success<User>
        val calendarResult = getCalendarsByUser(user.data.email)
        calendarResult
            .onSuccess { calendars ->
                _calendarListState.value = UiState.Success(calendars)
            }
            .onFailure { error ->
                _calendarListState.value = UiState.Error(error.message.orEmpty())
            }
    }

    suspend fun createCalendar() {
        if (validateFields()) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }

        val currentUser = authRepository.getCurrentUser().getOrNull()

        val calendar = Calendar(
            name = _calendarFormState.value.name,
            description = _calendarFormState.value.description,
            ownerId = currentUser?.uid!!,
            createdAt = Timestamp.now(),
            members = listOf(currentUser.email!!)
        )

        firestoreRepository.createCalendar(calendar)
            .onSuccess {
                calendar.uid = it
                val currentCalendars = (_calendarListState.value as? UiState.Success<List<Calendar>>)?.data.orEmpty()
                _calendarListState.value = UiState.Success(currentCalendars + calendar)
            }
            .onFailure {
                _calendarListState.value = UiState.Error(it.message.orEmpty())
            }
    }

    private suspend fun getUserInfo(uid: String): Result<User?> {
        return firestoreRepository.getUser(uid)
    }

    private suspend fun getCalendarsByUser(email: String): Result<List<Calendar>> {
        return firestoreRepository.getAllCalendarsByUser(email)
    }

    fun onNameChange(name: String) {
        _calendarFormState.value = _calendarFormState.value.copy(name = name)
    }

    fun onDescriptionChange(description: String) {
        _calendarFormState.value = _calendarFormState.value.copy(description = description)
    }

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_calendarFormState.value.name)
        val descriptionValidation = validateField(_calendarFormState.value.description)

        _calendarFormState.value = _calendarFormState.value.copy(
            nameError = nameValidation.errorMessage,
            descriptionError = descriptionValidation.errorMessage
        )

        return nameValidation.success && descriptionValidation.success
    }

    fun clearErrors() {
        _calendarFormState.value = _calendarFormState.value.copy(
            nameError = null,
            descriptionError = null
        )
    }

    fun clearFields() {
        _calendarFormState.value = CalendarFormState()
    }
}