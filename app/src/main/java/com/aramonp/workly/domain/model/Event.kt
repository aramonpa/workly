package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Event(
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val startDateTime: Timestamp = Timestamp.now(),
    val endDateTime: Timestamp = Timestamp.now(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val location: String? = null,
    val assignee: String = ""
)

fun Event.toMap(): Map<String, Any?> {
    return mapOf(
        "title" to title,
        "description" to description,
        "startDateTime" to startDateTime,
        "endDateTime" to endDateTime,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "location" to location,
        "assignee" to assignee
    )
}