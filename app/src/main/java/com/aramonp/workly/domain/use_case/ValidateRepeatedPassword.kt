package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult

class ValidateRepeatedPassword {
    operator fun invoke(password: String, repeatedPassword: String): ValidationResult {
        return if (password == repeatedPassword && password.isNotEmpty()) {
            ValidationResult(success = true)
        } else {
            ValidationResult(success = false, errorMessage = "Las contraseñas no coinciden.")
        }
    }
}