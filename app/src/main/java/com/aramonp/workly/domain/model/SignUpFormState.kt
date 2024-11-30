package com.aramonp.workly.domain.model

data class SignUpFormState(
    val name: String = "",
    val nameError: String? = null,
    val surname: String = "",
    val surnameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val username: String = "",
    val usernameError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: String? = null,
)
