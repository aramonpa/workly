package com.aramonp.workly.domain.model

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
    data class Success(val data: Any?) : AuthState()
    data class Error(val message: String) : AuthState()
}