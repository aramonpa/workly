package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult

class ValidateField {
    operator fun invoke(value: String): ValidationResult {
        return if (value.isNotEmpty()) {
            ValidationResult(success = true)
        } else {
            ValidationResult(success = false, errorMessage = "El campo no puede estar vacío.")
        }
    }
}