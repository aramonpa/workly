package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Calendar(
    var uid: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val members: List<String> = emptyList(),
    val events: List<Event>? = null
)

fun Calendar.toMap(): Map<String, Any?> {
    return mapOf(
        "name" to name,
        "description" to description,
        "ownerId" to ownerId,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "members" to members,
        "events" to events
    )
}