package com.aramonp.workly.data.repository

import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
)  : FirestoreRepository {
    override suspend fun createUser(user: User): FirestoreState<User> {
        return try {
            firebaseFirestore.collection("users")
                .add(user)
            FirestoreState.Success(user)
        } catch (e: Exception) {
            FirestoreState.Error("Ocurrió un error al crear el usuario")
        }
    }

    override suspend fun getUser(id: String): FirestoreState<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): FirestoreState<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: String): FirestoreState<User> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCalendarsByUser(userId: String): FirestoreState<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun createEvent(event: Event): FirestoreState<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEventsByCalendar(calendarId: String): FirestoreState<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(event: Event): FirestoreState<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(event: Event): FirestoreState<Event> {
        TODO("Not yet implemented")
    }

}