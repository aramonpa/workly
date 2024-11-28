package com.aramonp.workly.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.AuthRepositoryImpl
import com.aramonp.workly.data.repository.DataStoreRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {
    private val _notificationsState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val notificationsState: StateFlow<Boolean> = _notificationsState.asStateFlow()

    init {
        viewModelScope.launch {
            _notificationsState.value = getNotificationState()
        }
    }

    private suspend fun getNotificationState(): Boolean {
        return dataStoreRepository.getPreference("notifications") ?: false
    }

    suspend fun saveNotificationState(value: Boolean) {
        try {
            dataStoreRepository.savePreference("notifications", value)
            _notificationsState.value = value
        } catch (e: Exception) {
            _notificationsState.value = false
        }

    }

    suspend fun signOut() {
        authRepository.signOut()
    }
}