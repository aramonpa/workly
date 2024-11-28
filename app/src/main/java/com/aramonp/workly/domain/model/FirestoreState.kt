package com.aramonp.workly.domain.model

sealed class FirestoreState<T> {
    data class Success<T>(val data: T?) : FirestoreState<T>()
    data class Error<T>(val message: String) : FirestoreState<T>()
    class Loading<T> : FirestoreState<T>()
}