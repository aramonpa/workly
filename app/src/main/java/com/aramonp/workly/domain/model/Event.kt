package com.aramonp.workly.domain.model

data class Event(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val participants: List<String>
)
