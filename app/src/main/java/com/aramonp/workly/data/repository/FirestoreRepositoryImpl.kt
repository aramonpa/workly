package com.aramonp.workly.data.repository

import android.util.Log
import androidx.compose.ui.res.stringResource
import com.aramonp.workly.R
import com.aramonp.workly.domain.constant.FirestoreCollections
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.model.toMap
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.aramonp.workly.util.convertLocalDateToTimestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firebaseFirestore: FirebaseFirestore,
    private val resourceProvider: ResourceProviderImpl
)  : FirestoreRepository {
    override suspend fun createUser(user: User): Result<User> {
        val userResult = authRepository.getCurrentUser()

        return try {
            userResult
                .onFailure {
                    return Result.failure(Exception(it.message))
                }

            userResult.getOrNull()?.let {
                firebaseFirestore.collection(FirestoreCollections.USERS)
                    .document(it.uid)
                    .set(user)
                    .await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: String): Result<User?> {
        return try {
            Result.success(
                firebaseFirestore.collection(FirestoreCollections.USERS)
                    .document(id)
                    .get()
                    .await()
                    .toObject(User::class.java)
            )
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun updateUser(uid:String, userMap: Map<String, Any>): Result<Boolean> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.USERS)
                .document(uid)
                .update(userMap)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun getCalendar(calendarId: String): Result<Calendar?> {
        return try {
            Result.success(
                firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                    .document(calendarId)
                    .get()
                    .await()
                    .toObject(Calendar::class.java)
            )
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun createCalendar(calendar: Calendar): Result<String> {
        return try {
            val snapshot = firebaseFirestore
                .collection(FirestoreCollections.CALENDARS)
                .add(calendar.toMap())
                .await()
            Result.success(snapshot.id)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun getAllCalendarsByUser(email: String): Result<List<Calendar>> {
        return try {
            Result.success(
                firebaseFirestore
                    .collection(FirestoreCollections.CALENDARS)
                    .whereArrayContains("members", email)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents
                    .map { documentSnapshot ->
                        documentSnapshot.toCalendar()
                    }
            )
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun getEventInfo(calendarId: String, eventId: String): Result<Event?> {
        return try {
            Result.success(
                firebaseFirestore
                    .collection(FirestoreCollections.CALENDARS)
                    .document(calendarId)
                    .collection(FirestoreCollections.EVENTS)
                    .document(eventId)
                    .get()
                    .await()
                    .toObject(Event::class.java)
            )
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
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
            teams = (get("teams") as? List<*>)?.mapNotNull { it as? String }!!
        )
    }

    private fun DocumentSnapshot.toEvent(): Event {
        return Event(
            uid = id,
            title = getString("title")!!,
            description = getString("description")!!,
            startDateTime = getTimestamp("startDateTime")!!,
            endDateTime = getTimestamp("endDateTime")!!,
            createdAt = getTimestamp("createdAt"),
            updatedAt = getTimestamp("updatedAt"),
            location = getString("location"),
            assignee = getString("assignee")!!
        )
    }

    override suspend fun updateCalendar(calendarId: String, calendarMap: Map<String, Any>): Result<Boolean> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .update(calendarMap)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun addEvent(calendarId: String, event: Event): Result<Event> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .collection(FirestoreCollections.EVENTS)
                .add(event.toMap()).await()

            Result.success(event)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.message ?: resourceProvider.getString(R.string.unknown_error))
            Result.failure(e)
        }
    }

    override suspend fun getAllEventsByDay(calendarId: String, date: LocalDate): Result<List<Event>> {
        val startOfDay = convertLocalDateToTimestamp(date)
        val endOfDay = convertLocalDateToTimestamp(date.plusDays(1))
        return try {
            Result.success(
                firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                    .document(calendarId)
                    .collection(FirestoreCollections.EVENTS)
                    .whereLessThanOrEqualTo("startDateTime", endOfDay)
                    .whereGreaterThanOrEqualTo("endDateTime", startOfDay)
                    .get()
                    .await()
                    .documents
                    .map { documentSnapshot ->
                        documentSnapshot.toEvent()
                    }
            )
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(calendarId: String, eventId: String, eventMap: Map<String, Any?>): Result<Boolean> {
        return try {
            firebaseFirestore
                .collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .collection(FirestoreCollections.EVENTS)
                .document(eventId)
                .update(eventMap)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteCalendar(calendarId: String): Result<Boolean> {
        return try {
            firebaseFirestore
                .collection("calendars")
                .document(calendarId)
                .delete()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(calendarId: String, eventId: String): Result<Boolean> {
        return try {
            firebaseFirestore
                .collection("calendars")
                .document(calendarId)
                .collection(FirestoreCollections.EVENTS)
                .document(eventId)
                .delete()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun addTeamToCalendar(calendarId: String, teamName: String): Result<String> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .update("teams", FieldValue.arrayUnion(teamName))
            Result.success(teamName)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteTeamToCalendar(calendarId: String, teamName: String): Result<String> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .update("teams", FieldValue.arrayRemove(teamName))
            Result.success(teamName)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getTeams(calendarId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .get()
                .await()

            val teams = (documentSnapshot.get("teams") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

            Result.success(teams)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getMembers(calendarId: String): Result<List<String>> {
        return try {
            val documentSnapshot = firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .get()
                .await()

            val members = (documentSnapshot.get("members") as? List<*>)?.mapNotNull { it as? String }!!

            Result.success(members)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun addMemberToCalendar(calendarId: String, email: String): Result<String> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .update("members", FieldValue.arrayUnion(email))
            Result.success(email)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            val querySnapshot = firebaseFirestore.collection(FirestoreCollections.USERS)
                .whereEqualTo("email", email)
                .get()
                .await()

            val user = querySnapshot.documents.firstOrNull()?.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteMemberOfCalendar(calendarId: String, email: String): Result<Boolean> {
        return try {
            firebaseFirestore.collection(FirestoreCollections.CALENDARS)
                .document(calendarId)
                .update("members", FieldValue.arrayRemove(email))
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", e.stackTraceToString())
            Result.failure(e)
        }
    }

}