package com.aramonp.workly.domain.model

data class CalendarSettingsFormState (
    val name: String = "",
    val nameError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val team: String = "",
    val teamError: String? = null
)
