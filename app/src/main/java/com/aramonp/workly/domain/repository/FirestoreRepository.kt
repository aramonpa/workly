package com.aramonp.workly.domain.repository

import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.google.firebase.auth.FirebaseUser

interface FirestoreRepository {
    suspend fun createUser(user: User): Result<User>
    suspend fun getUser(id: String): Result<User?>
    suspend fun updateUser(uid:String, userMap: Map<String, Any>): Result<Boolean>
    suspend fun deleteUser(id: String): Result<User>
    suspend fun createCalendar(calendar: Calendar): Result<String>
    suspend fun getAllCalendarsByUser(uid: String): Result<List<Calendar>>
    suspend fun createEvent(event: Event): Result<Event>
    suspend fun getAllEventsByCalendar(calendarId: String): Result<List<User>>
    suspend fun updateEvent(event: Event): Result<Event>
    suspend fun deleteEvent(event: Event): Result<Event>
}