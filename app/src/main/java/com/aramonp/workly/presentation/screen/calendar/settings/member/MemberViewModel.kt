package com.aramonp.workly.presentation.screen.calendar.settings.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aramonp.workly.data.repository.FirestoreRepositoryImpl
import com.aramonp.workly.domain.model.Calendar
import com.aramonp.workly.domain.model.UiState
import com.aramonp.workly.domain.use_case.ValidateEmail
import com.aramonp.workly.domain.use_case.ValidateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepositoryImpl,
    private val validateEmail: ValidateEmail
): ViewModel() {
    private val _memberState = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val memberState: StateFlow<UiState<List<String>>> = _memberState

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _calendarId = MutableStateFlow("")

    suspend fun fetchMembers(calendarId: String) {
        _calendarId.value = calendarId
        getMembersInfo(calendarId)
            .onSuccess { members ->
                _memberState.value = UiState.Success(members)
            }
            .onFailure { error ->
                UiState.Error(error.message.orEmpty())
            }
    }

    private suspend fun getMembersInfo(calendarId: String): Result<List<String>> {
        return firestoreRepository.getMembers(calendarId)
    }

    suspend fun addMember(member: String) {
        if (!validateMember(member)) {
            return
        }

        if (!validateUser(member)) {
            return
        }

        firestoreRepository.addMemberToCalendar(
            _calendarId.value,
            member
        )
        onMemberChange(member, true)
    }

    private fun onMemberChange(member: String, add: Boolean) {
        val currentMembers =
            when (val state = _memberState.value) {
                is UiState.Success -> state.data.toMutableList()
                else -> mutableListOf()
        }

        if (add) {
            if (!currentMembers.contains(member)) {
                currentMembers.add(member)
            }
        } else {
            currentMembers.remove(member)
        }

        // Emitir el nuevo estado encapsulado en `UIState.Success`
        _memberState.value = UiState.Success(currentMembers)
    }

    suspend fun deleteMember(member: String) {
        firestoreRepository.deleteMemberOfCalendar(
            _calendarId.value,
            member
        )
        onMemberChange(member, false)

    }

    private suspend fun validateUser(member: String): Boolean {
        firestoreRepository.getUserByEmail(member)
            .onSuccess { value ->
                if (value != null) {
                    _nameError.value = null
                    return true
                } else {
                    _nameError.value = "No existe el usuario."
                    return false
                }
            }
            .onFailure {
            }

        val memberValidation = validateEmail(member)
        _nameError.value = memberValidation.errorMessage

        return memberValidation.success
    }

    private fun validateMember(member: String): Boolean {
        val memberValidation = validateEmail(member)
        _nameError.value = memberValidation.errorMessage

        return memberValidation.success
    }
}