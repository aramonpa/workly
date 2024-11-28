package com.aramonp.workly.presentation.screen.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
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

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _surnameError = MutableStateFlow<String?>(null)
    val surnameError: StateFlow<String?> = _surnameError

    private val _userNameError = MutableStateFlow<String?>(null)
    val userNameError: StateFlow<String?> = _userNameError

    private val _uid = MutableStateFlow("")

    init {
        fetchUser()
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

    private fun validateFields(user: User): Boolean {
        val nameValidation = validateField(user.name)
        val surnameValidation = validateField(user.surname)
        val userNameValidation = validateField(user.username)

        _nameError.value = nameValidation.errorMessage
        _surnameError.value = surnameValidation.errorMessage
        _userNameError.value = userNameValidation.errorMessage

        // Si hay errores, no continuar
        return nameValidation.success && surnameValidation.success
    }
}