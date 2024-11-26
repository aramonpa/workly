package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Event(
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val location: String = "",
    val assignee: String = ""
)

fun Event.toMap(): Map<String, Any?> {
    return mapOf(
        "title" to title,
        "description" to description,
        "startDate" to startDate,
        "endDate" to endDate,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "location" to location,
        "assignee" to assignee
    )
}