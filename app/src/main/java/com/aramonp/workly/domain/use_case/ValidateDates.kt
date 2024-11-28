package com.aramonp.workly.domain.use_case

import com.aramonp.workly.domain.model.ValidationResult
import com.google.firebase.Timestamp
import java.time.LocalDateTime

class ValidateDates {
    operator fun invoke(startDate: Timestamp, endDate: Timestamp): ValidationResult {
        if (endDate.toDate().before(startDate.toDate())) {
            return ValidationResult(false, "La fecha de finalización debe ser posterior a la fecha de inicio")
        }
        return ValidationResult(true)
    }
}