package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class Calendar(
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val members: List<String> = emptyList(),
    val events: List<Event>? = null
)
