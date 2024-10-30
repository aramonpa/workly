package com.aramonp.workly.data.repository

import androidx.lifecycle.LiveData
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): AuthState {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthState.Success(result.user)
        } catch (e: Exception) {
            AuthState.Error("Ocurrió un error al iniciar sesión.")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthState {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            AuthState.Success(result.user)
        } catch (e: Exception) {
            AuthState.Error("Ocurrió un error crear el usuario.")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): Result<FirebaseUser>? {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            Result.success(currentUser)
        } else {
            Result.failure(Exception("No hay usuario autenticado"))
        }
    }
}