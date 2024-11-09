package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class User(
    val name: String,
    val surname: String,
    val email: String,
    val username: String,
    val active: Int,
    val createdAt: Timestamp,
    val updatedAt: Timestamp?,
    val calendars: List<Calendar>?
)