package com.aramonp.workly.domain.model

data class CalendarEventFormState (
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val startDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val datesError: String? = null,
    val location: String? = null,
    val assignee: String = "",
    val assigneeError: String? = null
)
