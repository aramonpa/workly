package com.aramonp.workly.domain.model

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
    data class Success(val data: Any?) : AuthState()
    data class Error(val message: String) : AuthState()
}