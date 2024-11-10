package com.aramonp.workly.data.repository

import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firebaseFirestore: FirebaseFirestore
)  : FirestoreRepository {
    override suspend fun createUser(user: User): Result<User> {
        val userResult = authRepository.getCurrentUser()

        userResult
            .onFailure {
                return Result.failure(Exception("Error al identificar la sesión de usuario"))
            }

        return try {
            userResult.getOrNull()?.let {
                firebaseFirestore.collection("users")
                    .document(it.uid)
                    .set(user)
                    .await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Ocurrió un error al crear el usuario"))
        }
    }

    override suspend fun getUser(id: String): Result<User?> {
        return try {
            Result.success(
                firebaseFirestore.collection("users")
                    .document(id)
                    .get()
                    .await()
                    .toObject(User::class.java)
            )
        } catch (e: Exception) {
            val ess = e.message
            Result.failure(Exception("Ocurrió un error al obtener el usuario"))
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCalendarsByUser(calendarIds: List<String>): Result<List<Calendar>> {
        return try {
            Result.success(
                firebaseFirestore
                    .collection("calendars")
                    .whereIn(FieldPath.documentId(), calendarIds)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(Calendar::class.java) }
            )
        } catch (e: Exception) {
            Result.failure(Exception("Ocurrió un error al obtener el usuario"))
        }
    }

    override suspend fun createEvent(event: Event): Result<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEventsByCalendar(calendarId: String): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(event: Event): Result<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(event: Event): Result<Event> {
        TODO("Not yet implemented")
    }
}