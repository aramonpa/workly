package com.aramonp.workly.domain.repository

import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.Event
import com.aramonp.workly.domain.model.User
import java.time.LocalDate

interface FirestoreRepository {
    suspend fun createUser(user: User): Result<User>
    suspend fun getUser(id: String): Result<User?>
    suspend fun updateUser(uid: String, userMap: Map<String, Any>): Result<Boolean>
    suspend fun createCalendar(calendar: Calendar): Result<String>
    suspend fun getCalendar(calendarId: String): Result<Calendar?>
    suspend fun getAllCalendarsByUser(uid: String): Result<List<Calendar>>
    suspend fun updateCalendar(calendarId: String, calendarMap: Map<String, Any>): Result<Boolean>
    suspend fun addEvent(calendarId: String, event: Event): Result<Event>
    suspend fun getAllEventsByDay(calendarId: String, date: LocalDate): Result<List<Event>>
    suspend fun getEventInfo(calendarId: String, eventId: String): Result<Event?>
    suspend fun updateEvent(calendarId: String, eventId: String, eventMap: Map<String, Any>): Result<Boolean>
    suspend fun deleteEvent(calendarId: String, eventId: String): Result<Boolean>
    suspend fun deleteCalendar(calendarId: String): Result<Boolean>
    suspend fun addTeamToCalendar(calendarId: String, teamName: String): Result<String>
    suspend fun deleteTeamToCalendar(calendarId: String, teamName: String): Result<String>
    suspend fun getTeams(calendarId: String): Result<List<String>>
}