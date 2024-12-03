package com.aramonp.workly.domain.model

data class LogInFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)
