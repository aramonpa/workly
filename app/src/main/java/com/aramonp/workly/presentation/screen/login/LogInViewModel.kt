package com.aramonp.workly.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
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

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    suspend fun logIn() {
        _authState.emit(AuthState.Loading)

        val emailValidation = validateEmail(_email.value)
        val passwordValidation = validatePassword(_password.value)

        if (!emailValidation.success) {
            _emailError.value = emailValidation.errorMessage
        }

        if (!passwordValidation.success) {
            _passwordError.value = passwordValidation.errorMessage
        }

        if (!emailValidation.success || !passwordValidation.success) {
            _authState.emit(AuthState.Unauthenticated)
            return
        }

        authRepository.signIn(_email.value, _password.value)
            .onSuccess {
                _authState.emit(AuthState.Success(it))
            }
            .onFailure {
                _authState.emit(AuthState.Error(it.message ?: "Error desconocido"))
            }
    }

    fun onEmailChange(email: String) {
        _email.value = email
        _emailError.value = null
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        _passwordError.value = null
    }
}