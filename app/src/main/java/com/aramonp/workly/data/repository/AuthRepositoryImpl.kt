package com.aramonp.workly.data.repository

import android.util.Log
import com.aramonp.workly.R
import com.aramonp.workly.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProviderImpl
) : AuthRepository {
    /*
    TODO: Change Result for DataState and Flow
     https://github.com/piashcse/Hilt-MVVM-Compose-Movie/blob/master/app/src/main/java/com/piashcse/hilt_mvvm_compose_movie/utils/network/DataState.kt
     */
    override suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): Result<FirebaseUser?> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            Result.success(currentUser)
        } else {
            Result.failure(Exception("No existe un usuario autenticado"))
        }
    }
}