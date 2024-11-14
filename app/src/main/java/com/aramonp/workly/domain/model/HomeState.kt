package com.aramonp.workly.domain.model

sealed class HomeState<out T> {
    data class Success<out T>(val data: T) : HomeState<T>()
    data class Error(val message: String) : HomeState<Nothing>()
    data object Loading : HomeState<Nothing>()
}