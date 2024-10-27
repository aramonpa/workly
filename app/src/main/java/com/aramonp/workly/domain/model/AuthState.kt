package com.aramonp.workly.domain.model

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}