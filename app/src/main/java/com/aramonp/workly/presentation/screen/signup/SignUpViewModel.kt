package com.aramonp.workly.presentation.screen.signup

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _surname = MutableStateFlow("")
    val surname: StateFlow<String> = _surname

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onSurnameChange(surname: String) {
        _surname.value = surname
    }

    fun onUsernameChange(username: String) {
        _username.value = username
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    suspend fun signUp() {
        _authState.value = AuthState.Loading

        val user = User(
            name = _name.value,
            surname = _surname.value,
            username = _username.value,
            email = _email.value,
            active = 1,
            createdAt = Timestamp.now(),
            updatedAt = null
        )

        val authResult = authRepository.signUp(user.email.orEmpty(), password.value)

        authResult
            .onSuccess {
                val firestoreResult = firestoreRepository.createUser(user)
                firestoreResult
                    .onSuccess {
                        _authState.value = AuthState.Success(firestoreResult.getOrNull())
                    }
                    .onFailure {
                        _authState.value = AuthState.Error(it.message.orEmpty())
                    }
            }
            .onFailure {
                _authState.value = AuthState.Error(it.message.orEmpty())
            }
    }
}