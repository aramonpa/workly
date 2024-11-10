package com.aramonp.workly.presentation.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.FirestoreState
import com.aramonp.workly.domain.model.HomeState
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
    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> = _homeState

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            val authResult = authRepository.getCurrentUser()
            authResult.fold(
                onSuccess = { user -> handleUser(user) },
                onFailure = { error ->
                    HomeState.Error(error.message.orEmpty())
                }
            )
        }
    }

    private suspend fun handleUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.uid?.let { uid ->
            getUserInfo(uid)
                .onSuccess { user ->
                    if (user!!.memberOf.isNullOrEmpty()) {
                        _homeState.value = HomeState.Success(user, emptyList())
                    } else {
                        fetchUserCalendars(user)
                    }
                }
                .onFailure { error ->
                    HomeState.Error(error.message.orEmpty())
                }
        }
    }

    private suspend fun fetchUserCalendars(user: User) {
        val calendarResult = getCalendarsByUser(user.memberOf ?: emptyList())
        calendarResult
            .onSuccess { calendars ->
                _homeState.value = HomeState.Success(user, calendars ?: emptyList())
            }
            .onFailure { error ->
                HomeState.Error(error.message.orEmpty())
            }
    }

    // TODO: Review if it's better to use simple Types
    private suspend fun getUserInfo(uid: String): Result<User?> {
        return firestoreRepository.getUser(uid)
    }

    private suspend fun getCalendarsByUser(calendarIds: List<String>): Result<List<Calendar>?> {
        return if (calendarIds.isEmpty()) {
            Result.success(emptyList())
        } else {
            firestoreRepository.getAllCalendarsByUser(calendarIds)
        }
    }
}