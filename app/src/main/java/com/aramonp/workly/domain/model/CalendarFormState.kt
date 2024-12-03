package com.aramonp.workly.domain.model

data class CalendarFormState(
    val name: String = "",
    val nameError: String? = null,
    val description: String = "",
    val descriptionError: String? = null
)