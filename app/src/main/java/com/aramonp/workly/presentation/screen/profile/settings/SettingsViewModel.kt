package com.aramonp.workly.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.SettingsFormState
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
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField
) : ViewModel() {
    private val _settingsState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val settingsState: StateFlow<UiState<User>> = _settingsState

    private val _settingsFormState = MutableStateFlow(SettingsFormState())
    val settingsFormState: StateFlow<SettingsFormState> = _settingsFormState

    private val _validationState = MutableStateFlow(false)
    val validationState: StateFlow<Boolean> = _validationState

    private val _uid = MutableStateFlow("")

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            val authResult = authRepository.getCurrentUser()
            authResult
                .onSuccess { user -> handleUser(user) }
                .onFailure { error ->
                    UiState.Error(error.message.orEmpty())
                }
        }
    }

    private suspend fun handleUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.uid?.let { uid ->
            _uid.value = uid
            getUserInfo(uid)
                .onSuccess { user ->
                    fillFormState(user!!)
                    _settingsState.value = UiState.Success(user)
                }
                .onFailure { error ->
                    UiState.Error(error.message.orEmpty())
                }
        }
    }

    private suspend fun getUserInfo(uid: String): Result<User?> {
        return firestoreRepository.getUser(uid)
    }

    suspend fun updateUserInfo() {
        if (validateFields()) {
            _validationState.value = true
        } else {
            _validationState.value = false
            return
        }

        firestoreRepository.updateUser(
            _uid.value,
            mapOf(
                "name" to _settingsFormState.value.name,
                "surname" to _settingsFormState.value.surname,
                "username" to _settingsFormState.value.username,
                "updatedAt" to Timestamp.now()
            )
        )
        onUserFieldChange { calendar -> calendar.copy(name = _settingsFormState.value.name) }
        onUserFieldChange { calendar -> calendar.copy(surname = _settingsFormState.value.surname) }
        onUserFieldChange { calendar -> calendar.copy(username = _settingsFormState.value.username) }
    }

    private fun fillFormState(user: User) {
        _settingsFormState.value = _settingsFormState.value.copy(name = user.name)
        _settingsFormState.value = _settingsFormState.value.copy(surname = user.surname)
        _settingsFormState.value = _settingsFormState.value.copy(username = user.username)
    }

    private fun onUserFieldChange(fieldUpdater: (User) -> User) {
        _settingsState.value = _settingsState.value.let {
            when (it) {
                is UiState.Success -> {
                    val updatedUser = fieldUpdater(it.data)
                    UiState.Success(updatedUser)
                }
                else -> it
            }
        }
    }

    fun onNameChange(name: String) {
        _settingsFormState.value = _settingsFormState.value.copy(name = name)
    }

    fun onSurnameChange(surname: String) {
        _settingsFormState.value = _settingsFormState.value.copy(surname = surname)
    }

    fun onUsernameChange(username: String) {
        _settingsFormState.value = _settingsFormState.value.copy(username = username)
    }

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_settingsFormState.value.name)
        val surnameValidation = validateField(_settingsFormState.value.surname)
        val usernameValidation = validateField(_settingsFormState.value.username)

        _settingsFormState.value = _settingsFormState.value.copy(
            nameError = nameValidation.errorMessage,
            surnameError = surnameValidation.errorMessage,
            usernameError = usernameValidation.errorMessage
        )

        return nameValidation.success && surnameValidation.success && usernameValidation.success
    }

    fun clearErrors() {
        _settingsFormState.value = _settingsFormState.value.copy(
            nameError = null,
            surnameError = null,
            usernameError = null
        )
    }
}