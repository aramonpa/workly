package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult

class ValidatePassword {
    operator fun invoke(password: String): ValidationResult {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { "!@#$%^&*()-_=+[]{}|;:',.<>?".contains(it) }
        val isValid = password.length >= 8 && hasUppercase && hasDigit && hasSpecialChar

        return if (isValid) {
            ValidationResult(success = true)
        } else {
            ValidationResult(
                success = false,
                errorMessage = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial"
            )
        }
    }
}