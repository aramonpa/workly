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
                    _settingsState.value = UiState.Success(user!!)
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
        val state = (settingsState.value as UiState.Success<User>)

        if (!validateFields(state.data)) {
            return
        }

        firestoreRepository.updateUser(
            _uid.value,
            mapOf(
                "name" to state.data.name,
                "surname" to state.data.surname,
                "username" to state.data.username,
                "updatedAt" to Timestamp.now()
            )
        )
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
        onUserFieldChange { user -> user.copy(name = name) }
    }

    fun onSurnameChange(surname: String) {
        onUserFieldChange { user -> user.copy(surname = surname) }
    }

    fun onUsernameChange(username: String) {
        onUserFieldChange { user -> user.copy(username = username) }
    }

    private fun validateFields(user: User): Boolean {
        val nameValidation = validateField(user.name)
        val surnameValidation = validateField(user.surname)
        val usernameValidation = validateField(user.username)

        _settingsFormState.value = _settingsFormState.value.copy(
            nameError = nameValidation.errorMessage,
            surnameError = surnameValidation.errorMessage,
            usernameError = usernameValidation.errorMessage
        )

        return nameValidation.success && surnameValidation.success
    }
}