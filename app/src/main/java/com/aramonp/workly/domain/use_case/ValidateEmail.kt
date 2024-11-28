package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult

class ValidateEmail {
    operator fun invoke(email: String): ValidationResult {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ValidationResult(success = true)
        } else {
            ValidationResult(success = false, errorMessage = "El correo no es válido")
        }
    }
}