package com.lost.watch.feature.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ObserveAuthorizedContactUseCase
import com.lost.domain.usecase.RevokeAuthorizedContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ContactsViewModel @Inject constructor(
    observeContact: ObserveAuthorizedContactUseCase,
    private val revokeContact: RevokeAuthorizedContactUseCase
) : ViewModel() {
    private val isRevokeArmed = MutableStateFlow(false)

    val uiState: StateFlow<ContactsUiState> = combine(observeContact(), isRevokeArmed) { contact, armed ->
        if (contact == null) {
            ContactsUiState.EMPTY
        } else {
            ContactsUiState(
                contact.name,
                contact.relationship,
                contact.phoneNumber,
                contact.hasAcceptedInvitation,
                armed
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ContactsUiState.EMPTY)

    fun onEvent(event: ContactsUiEvent) {
        when (event) {
            ContactsUiEvent.RevokeTapped -> if (isRevokeArmed.value) {
                isRevokeArmed.value = false
                viewModelScope.launch { revokeContact() }
            } else {
                isRevokeArmed.value = true
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
