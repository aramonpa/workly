package com.aramonp.workly.domain.model

data class Calendar(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val permissions: List<String>? = null,
    val events: List<Event>? = null
)
