package com.aramonp.workly.presentation.screen.signup

import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.SignUpFormState
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

    private val _signUpFormState = MutableStateFlow(SignUpFormState())
    val signUpFormState: StateFlow<SignUpFormState> = _signUpFormState

    suspend fun signUp() {
        _authState.value = AuthState.Loading

        if (!validateFields()) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        val user = User(
            name = _signUpFormState.value.name,
            surname = _signUpFormState.value.surname,
            username = _signUpFormState.value.username,
            email = _signUpFormState.value.email,
            active = 1,
            createdAt = Timestamp.now(),
            updatedAt = null
        )

        val authResult = authRepository.signUp(user.email, _signUpFormState.value.password)

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

    fun onNameChange(name: String) {
        _signUpFormState.value = _signUpFormState.value.copy(name = name)
    }

    fun onSurnameChange(surname: String) {
        _signUpFormState.value = _signUpFormState.value.copy(surname = surname)
    }

    fun onUsernameChange(username: String) {
        _signUpFormState.value = _signUpFormState.value.copy(username = username)
    }

    fun onEmailChange(email: String) {
        _signUpFormState.value = _signUpFormState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _signUpFormState.value = _signUpFormState.value.copy(password = password)
    }

    fun onRepeatedPasswordChange(password: String) {
        _signUpFormState.value = _signUpFormState.value.copy(confirmPassword = password)
    }

    private fun validateFields(): Boolean {
        val nameValidation = validateField(_signUpFormState.value.name)
        val surnameValidation = validateField(_signUpFormState.value.surname)
        val usernameValidation = validateField(_signUpFormState.value.username)
        val emailValidation = validateEmail(_signUpFormState.value.email)
        val passwordValidation = validatePassword(_signUpFormState.value.password)
        val repeatedPasswordValidation = validateRepeatedPassword(_signUpFormState.value.password, _signUpFormState.value.confirmPassword)

        _signUpFormState.value = _signUpFormState.value.copy(
            nameError = nameValidation.errorMessage,
            surnameError = surnameValidation.errorMessage,
            usernameError = usernameValidation.errorMessage,
            emailError = emailValidation.errorMessage,
            passwordError = passwordValidation.errorMessage,
            confirmPasswordError = repeatedPasswordValidation.errorMessage
        )

        return nameValidation.success && surnameValidation.success && usernameValidation.success &&
            emailValidation.success && passwordValidation.success && repeatedPasswordValidation.success
    }
}