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

    private val _userState = MutableLiveData<FirestoreState<User>>()
    val userState: LiveData<FirestoreState<User>> = _userState

    private val _calendarState = MutableLiveData<FirestoreState<List<Calendar>>>()
    val calendarState: LiveData<FirestoreState<List<Calendar>>> = _calendarState

    init {
        // Establecer el estado de carga al principio
        _userState.value = FirestoreState.Loading()
        _calendarState.value = FirestoreState.Loading()

        viewModelScope.launch {
            val authUser = authRepository.getCurrentUser()
            authUser
                .onSuccess {
                    val userInfo = getUserInfo(it!!.uid)

                    userInfo
                        .onSuccess { user ->
                            _userState.value = FirestoreState.Success(user)

                            if (!user!!.memberOf.isNullOrEmpty()) {
                                getCalendarsByUser(user.memberOf!!)
                                    .onSuccess { calendarList ->
                                        _calendarState.value = FirestoreState.Success(calendarList)
                                    }
                            } else {
                                _calendarState.value = FirestoreState.Success(emptyList())
                            }
                        }
                        .onFailure { userError ->
                            _calendarState.value = userError.message?.let { userError1 -> FirestoreState.Error(userError1) }
                        }
                }.onFailure {
                    _userState.value = it.message?.let { it1 -> FirestoreState.Error(it1) }
                }

        }
    }

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