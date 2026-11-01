package com.lost.watch.feature.contacts

import com.lost.domain.model.DeviceContact

/** Elegir contacto: cargando, sin permiso para ver los contactos, la lista (puede estar vacía) o ya elegido. */
sealed interface ContactPickerUiState {
    data object Loading : ContactPickerUiState

    data object NeedsPermission : ContactPickerUiState

    data class Contacts(val contacts: List<DeviceContact>) : ContactPickerUiState

    data object Chosen : ContactPickerUiState
}
