package com.aramonp.workly.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<FirebaseUser?>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?>
    suspend fun signOut()
    suspend fun getCurrentUser(): Result<FirebaseUser?>
}