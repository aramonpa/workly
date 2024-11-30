package com.aramonp.workly.domain.model

data class SettingsFormState(
    val name: String = "",
    val nameError: String? = null,
    val surname: String = "",
    val surnameError: String? = null,
    val username: String = "",
    val usernameError: String? = null
)
