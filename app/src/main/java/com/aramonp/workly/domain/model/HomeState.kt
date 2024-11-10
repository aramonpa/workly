package com.aramonp.workly.domain.model

sealed class HomeState {
    data class Success(val user: User, val calendarList: List<Calendar>) : HomeState()
    data class Error(val message: String) : HomeState()
    data object Loading : HomeState()
}