package com.aramonp.workly.domain.model

import com.google.firebase.Timestamp

data class User(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val username: String = "",
    val active: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)