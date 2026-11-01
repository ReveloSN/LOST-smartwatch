package com.lost.watch.feature.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.error.LostResult
import com.lost.domain.usecase.AuthorizeDeviceContactUseCase
import com.lost.domain.usecase.GetDeviceContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ContactPickerViewModel @Inject constructor(
    private val getContacts: GetDeviceContactsUseCase,
    private val authorize: AuthorizeDeviceContactUseCase
) : ViewModel() {
    private val state = MutableStateFlow<ContactPickerUiState>(ContactPickerUiState.Loading)
    val uiState: StateFlow<ContactPickerUiState> = state.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: ContactPickerUiEvent) {
        when (event) {
            ContactPickerUiEvent.PermissionAnswered -> load()

            is ContactPickerUiEvent.Choose -> viewModelScope.launch {
                authorize(event.contact)
                state.value = ContactPickerUiState.Chosen
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            state.value = when (val result = getContacts()) {
                is LostResult.Success -> ContactPickerUiState.Contacts(result.value)
                is LostResult.Failure -> ContactPickerUiState.NeedsPermission
            }
        }
    }
}
