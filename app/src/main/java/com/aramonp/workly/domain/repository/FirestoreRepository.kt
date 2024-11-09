package com.aramonp.workly.domain.repository

import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.google.firebase.auth.FirebaseUser

interface FirestoreRepository {
    suspend fun createUser(user: User): FirestoreState<User>
    suspend fun getUser(id: String): FirestoreState<User>
    suspend fun updateUser(user: User): FirestoreState<User>
    suspend fun deleteUser(id: String): FirestoreState<User>
    suspend fun getAllCalendarsByUser(calendarIds: List<String>): FirestoreState<List<Calendar>>
    suspend fun createEvent(event: Event): FirestoreState<Event>
    suspend fun getAllEventsByCalendar(calendarId: String): FirestoreState<List<User>>
    suspend fun updateEvent(event: Event): FirestoreState<Event>
    suspend fun deleteEvent(event: Event): FirestoreState<Event>
}