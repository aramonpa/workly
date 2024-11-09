package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.User
import com.aramonp.workly.domain.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val firestoreRepository: FirestoreRepositoryImpl
) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _calendars = MutableLiveData<List<Calendar>>()
    val calendars: LiveData<List<Calendar>> = _calendars

    private val _firestoreState = MutableLiveData<FirestoreState<Any>>()
    val firestoreState: LiveData<FirestoreState<Any>> = _firestoreState

    init {
        viewModelScope.launch {
            val authUser = authRepository.getCurrentUser()

            if (authUser.isSuccess) {
                val userState = authUser.getOrNull()?.let { getUser(it.uid) }

                when (userState) {
                    is FirestoreState.Success -> {
                        val calendarState =
                            userState.data?.memberOf?.let { getCalendarsByUser(it) }

                        when (calendarState) {
                            is FirestoreState.Success -> {
                                _firestoreState.value = FirestoreState.Success(
                                    Pair(userState, calendarState)
                                )
                            }

                            is FirestoreState.Error -> {
                                _firestoreState.value = FirestoreState.Error(
                                    "Error obtaining Calendars"
                                )
                            }

                            else -> {
                                _firestoreState.value = FirestoreState.Error(
                                    "Inesperated error obtaining Calendars"
                                )

                            }
                        }
                    }

                    is FirestoreState.Error -> {
                        _firestoreState.value = FirestoreState.Error(
                            "Error obtaining user info"
                        )
                    }

                    else -> {
                        _firestoreState.value = FirestoreState.Error(
                            "Inesperated error obtaining user info"
                        )

                    }
                }
            } else {
                _firestoreState.value = FirestoreState.Error(
                    "Error obtaining user info"
                )
            }
        }
    }

    private suspend fun getUser(uid: String): FirestoreState<User> {
        return firestoreRepository.getUser(uid)
    }

    private suspend fun getCalendarsByUser(calendarIds: List<String>): FirestoreState<List<Calendar>> {
        return if (calendarIds.isEmpty()) {
            FirestoreState.Success(emptyList())
        } else {
            firestoreRepository.getAllCalendarsByUser(calendarIds)
        }
    }
}