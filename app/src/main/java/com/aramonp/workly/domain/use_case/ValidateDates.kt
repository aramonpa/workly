package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult
import com.google.firebase.Timestamp
import java.time.LocalDateTime

class ValidateDates {
    operator fun invoke(startDate: Timestamp?, endDate: Timestamp?): ValidationResult {
        if (startDate == null) {
            return ValidationResult(false, "La fecha/hora de inicio no puede estar vacía")
        }
        if (endDate == null) {
            return ValidationResult(false, "La fecha/hora de finalización no puede estar vacía")
        }

        // Validar que la fecha final sea posterior a la inicial
        if (endDate.toDate().before(startDate.toDate())) {
            return ValidationResult(false, "La fecha de finalización debe ser posterior a la fecha de inicio")
        }

        return ValidationResult(true)
    }
}