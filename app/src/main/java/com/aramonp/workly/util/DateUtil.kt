package com.aramonp.workly.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun combineDateAndTime(date: String, time: String): LocalDateTime {
    return LocalDateTime.of(
        LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
        LocalTime.parse(time, DateTimeFormatter.ISO_TIME)
    )
}

fun convertToTimestamp(dateTime: LocalDateTime): Timestamp {
    val instant = dateTime.toInstant(ZoneOffset.UTC) // Convierte LocalDateTime a Instant
    return Timestamp(instant.epochSecond, instant.nano)
}

fun convertLocalDateToTimestamp(date: LocalDate): Timestamp {
    val instant = date.atStartOfDay(ZoneOffset.UTC).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}

fun getTimeFromTimeStamp(datetime: Timestamp): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(datetime.toDate())
}

fun getDateFromTimeStamp(datetime: Timestamp): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(datetime.toDate())
}