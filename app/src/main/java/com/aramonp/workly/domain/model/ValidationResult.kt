package com.aramonp.workly.domain.model

data class ValidationResult(
    val success: Boolean,
    val errorMessage: String? = null
)