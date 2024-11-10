package com.aramonp.workly.presentation.screen.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    fun checkAuthStatus() {
        _authState.value = AuthState.Authenticated
    }

    suspend fun logIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        if (!isEmailValid(email) && !isPasswordValid(password)) {
            _authState.value = AuthState.Error("Email o contraseña incorrectos")
        }

        authRepository.signIn(email, password)
            .onSuccess {
                _authState.value = AuthState.Success(it)
            }
            .onFailure {
                _authState.value = it.message?.let { it1 -> AuthState.Error(it1) }
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