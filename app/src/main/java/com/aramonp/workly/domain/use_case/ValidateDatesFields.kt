package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult

class ValidateDatesFields {
    operator fun invoke(
        startDate: String?,
        startTime: String?,
        endDate: String?,
        endTime: String?
    ): ValidationResult {
        if (startDate.isNullOrBlank()) {
            return ValidationResult(false, "La fecha de inicio es obligatoria")
        }
        if (startTime.isNullOrBlank()) {
            return ValidationResult(false, "La hora de inicio es obligatoria")
        }
        if (endDate.isNullOrBlank()) {
            return ValidationResult(false, "La fecha de finalización es obligatoria")
        }
        if (endTime.isNullOrBlank()) {
            return ValidationResult(false, "La hora de finalización es obligatoria")
        }
        return ValidationResult(true)
    }
}