package com.aramonp.workly.presentation.screen.signup

import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.use_case.ValidateEmail
import com.aramonp.workly.domain.use_case.ValidateField
import com.aramonp.workly.domain.use_case.ValidatePassword
import com.aramonp.workly.domain.use_case.ValidateRepeatedPassword
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateField: ValidateField,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _repeatedPassword = MutableStateFlow("")
    val repeatedPassword: StateFlow<String> = _repeatedPassword

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _surnameError = MutableStateFlow<String?>(null)
    val surnameError: StateFlow<String?> = _surnameError

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _repeatedPasswordError = MutableStateFlow<String?>(null)
    val repeatedPasswordError: StateFlow<String?> = _repeatedPasswordError


    fun onNameChange(name: String) {
        _user.value = _user.value.copy(name = name)
    }

    fun onSurnameChange(surname: String) {
        _user.value = _user.value.copy(surname = surname)
    }

    fun onUsernameChange(username: String) {
        _user.value = _user.value.copy(username = username)
    }

    fun onEmailChange(email: String) {
        _user.value = _user.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onRepeatedPasswordChange(password: String) {
        _repeatedPassword.value = password
    }

    suspend fun signUp() {
        _authState.value = AuthState.Loading

        if (!validateFields()) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        val user = User(
            name = _user.value.name,
            surname = _user.value.surname,
            username = _user.value.username,
            email = _user.value.email,
            active = 1,
            createdAt = Timestamp.now(),
            updatedAt = null
        )

        val authResult = authRepository.signUp(user.email, password.value)

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

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_user.value.name)
        val surnameValidation = validateField(_user.value.surname)
        val usernameValidation = validateField(_user.value.username)
        val emailValidation = validateEmail(_user.value.email)
        val passwordValidation = validatePassword(_password.value)
        val repeatedPasswordValidation = validateRepeatedPassword(_password.value, _repeatedPassword.value)

        _nameError.value = nameValidation.errorMessage
        _surnameError.value = surnameValidation.errorMessage
        _usernameError.value = usernameValidation.errorMessage
        _emailError.value = emailValidation.errorMessage
        _passwordError.value = passwordValidation.errorMessage
        _repeatedPasswordError.value = repeatedPasswordValidation.errorMessage

        // Si hay errores, no continuar
        return nameValidation.success && surnameValidation.success && usernameValidation.success &&
            emailValidation.success && passwordValidation.success && repeatedPasswordValidation.success
    }
}