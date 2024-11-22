package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Event(
    val title: String,
    val description: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
    val location: String,
    val participant: String
)
