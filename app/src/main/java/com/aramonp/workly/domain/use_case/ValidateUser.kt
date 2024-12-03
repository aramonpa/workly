package com.aramonp.workly.domain.use_case

import androidx.lifecycle.MutableLiveData
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.ValidationResult
import com.aramonp.workly.domain.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ValidateUser @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateEmail: ValidateEmail,
    private val validateField: ValidateField
) {
    suspend operator fun invoke(member: String): ValidationResult {
        val fieldValidation = validateField(member)
        if (!fieldValidation.success) {
            return ValidationResult(success = false, errorMessage = fieldValidation.errorMessage)
        }

        val emailValidation = validateEmail(member)
        if (!emailValidation.success) {
            return ValidationResult(success = false, errorMessage = emailValidation.errorMessage)
        }

        return try {
            val userResult = firestoreRepository.getUserByEmail(member)
            userResult.fold(
                onSuccess = { value ->
                    if (value != null) {
                        ValidationResult(success = true)
                    } else {
                        ValidationResult(success = false, errorMessage = "No existe el usuario.")
                    }
                },
                onFailure = {
                    ValidationResult(success = false, errorMessage = "Error al consultar el usuario.")
                }
            )
        } catch (e: Exception) {
            ValidationResult(success = false, errorMessage = "Ocurrió un error inesperado.")
        }
    }
}