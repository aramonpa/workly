package com.aramonp.workly.data.repository

import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.model.toMap
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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
            val t = e.message
            Result.failure(Exception("Ocurrió un error al obtener el usuario"))
        }
    }

    override suspend fun updateUser(uid:String, userMap: Map<String, Any>): Result<Boolean> {
        return try {
            firebaseFirestore.collection("users")
                .document(uid)
                .update(userMap)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Ocurrió un error al obtener el usuario"))
        }
    }

    override suspend fun deleteUser(id: String): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun createCalendar(calendar: Calendar): Result<String> {
        return try {
            val snapshot = firebaseFirestore
                .collection("calendars")
                .add(calendar.toMap())
                .await()
            Result.success(snapshot.id)
        } catch (e: Exception) {
            Result.failure(Exception("Ocurrió un error al crear el calendario"))
        }
    }

    override suspend fun getAllCalendarsByUser(uid: String): Result<List<Calendar>> {
        return try {
            Result.success(
                firebaseFirestore
                    .collection("calendars")
                    .whereArrayContains("members", uid)
                    .get()
                    .await()
                    .documents
                    .map { documentSnapshot ->
                        documentSnapshot.toCalendar()
                    }
            )
        } catch (e: Exception) {
            Result.failure(Exception("Ocurrió un error al obtener el usuario"))
        }
    }

    private fun DocumentSnapshot.toCalendar(): Calendar {
        return Calendar(
            uid = id,
            name = getString("name")!!,
            description = getString("description")!!,
            ownerId = getString("ownerId")!!,
            createdAt = getTimestamp("createdAt")!!,
            updatedAt = getTimestamp("updatedAt"),
            members = (get("members") as? List<*>)?.mapNotNull { it as? String }!!,
            events = (get("events") as? List<*>)?.mapNotNull { it as? Event }
        )
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