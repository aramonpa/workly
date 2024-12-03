package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class EventFormState(
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val startDateTime: Timestamp = Timestamp.now(),
    val endDateTime: Timestamp = Timestamp.now(),
    val datesError: String? = null,
    val location: String? = null,
    val assignee: String = "",
    val assigneeError: String? = null
)
