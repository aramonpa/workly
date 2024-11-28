package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Calendar(
    var uid: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val teams: List<String> = emptyList(),
    val members: List<String> = emptyList()
)

fun Calendar.toMap(): Map<String, Any?> {
    return mapOf(
        "name" to name,
        "description" to description,
        "ownerId" to ownerId,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "members" to members,
        "teams" to teams
    )
}