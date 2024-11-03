package com.aramonp.workly.domain.model

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val username: String,
    val password: String,
    val calendars: List<Calendar>
)