package com.aramonp.workly.presentation.screen.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.User
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

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
        _authState.value = authRepository.signUp(user)
    }

    suspend fun saveUser(user: User) {

    }
}