package com.aramonp.workly.presentation.screen.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun checkAuthStatus() {
        _authState.value = AuthState.Authenticated
    }

    suspend fun logIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.emit(AuthState.Loading)
            if (!isEmailValid(email) || !isPasswordValid(password)) {
                _authState.emit(AuthState.Error("Email o contraseña incorrectos"))
                return@launch
            }

            authRepository.signIn(email, password)
                .onSuccess {
                    _authState.emit(AuthState.Success(it))
                }
                .onFailure {
                    _authState.emit(AuthState.Error(it.message ?: "Error desconocido"))
                }
        }
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}