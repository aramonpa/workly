package com.aramonp.workly.domain.repository

import com.aramonp.workly.domain.model.AuthState
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthState
    suspend fun signUp(email: String, password: String): AuthState
    suspend fun signOut()
    suspend fun getCurrentUser(): Result<FirebaseUser>?
}