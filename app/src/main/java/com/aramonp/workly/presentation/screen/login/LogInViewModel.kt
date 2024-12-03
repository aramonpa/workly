package com.aramonp.workly.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.LogInFormState
import com.aramonp.workly.domain.use_case.ValidateEmail
import com.aramonp.workly.domain.use_case.ValidatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword

) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _logInFormState = MutableStateFlow(LogInFormState())
    val logInFormState: StateFlow<LogInFormState> = _logInFormState

    suspend fun logIn() {
        _authState.emit(AuthState.Loading)

        if (!validateFields()) {
            _authState.emit(AuthState.Unauthenticated)
            return
        }

        authRepository.signIn(_logInFormState.value.email, _logInFormState.value.password)
            .onSuccess {
                _authState.emit(AuthState.Success(it))
            }
            .onFailure {
                _authState.emit(AuthState.Error(it.message.orEmpty()))
            }
    }

    fun onEmailChange(email: String) {
        _logInFormState.value = _logInFormState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _logInFormState.value = _logInFormState.value.copy(password = password)
    }

    private fun validateFields(): Boolean {
        val emailValidation = validateEmail(_logInFormState.value.email)
        val passwordValidation = validatePassword(_logInFormState.value.password)

        _logInFormState.value = _logInFormState.value.copy(
            emailError = emailValidation.errorMessage,
            passwordError = passwordValidation.errorMessage
        )

        return emailValidation.success && passwordValidation.success
    }
}