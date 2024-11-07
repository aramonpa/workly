package com.aramonp.workly.presentation.screen.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _firestoreState = MutableLiveData<FirestoreState<User>>()
    val firestoreState: LiveData<FirestoreState<User>> = _firestoreState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    fun onUserChange(user: User) {
        _user.value = user
    }

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

    suspend fun signUp(user: User) {
        _authState.value = AuthState.Loading

        try {
            // Paso 1: Realizar la autenticación
            val authResult = password.value?.let { authRepository.signUp(user.email, it) }
            if (authResult is AuthState.Success) {
                // Paso 2: Crear usuario en Firestore solo si la autenticación fue exitosa
                val firestoreResult = firestoreRepository.createUser(user)
                _firestoreState.value = firestoreResult

                // Actualizar el estado de autenticación según el resultado de Firestore
                _authState.value = if (firestoreResult is FirestoreState.Success) {
                    authResult
                } else {
                    AuthState.Error("Error creating user in Firestore")
                }
            } else {
                // Si la autenticación falló, actualizar el estado con el error
                _authState.value = AuthState.Error("Authentication failed")
            }
        } catch (e: Exception) {
            // Manejo de excepciones, si ocurre algún error inesperado
            _authState.value = AuthState.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}