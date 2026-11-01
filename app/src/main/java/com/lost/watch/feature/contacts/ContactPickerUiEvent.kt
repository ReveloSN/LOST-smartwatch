package com.lost.watch.feature.contacts

import com.lost.domain.model.DeviceContact

/** Acciones al elegir el contacto autorizado. */
sealed interface ContactPickerUiEvent {
    /** El permiso de contactos se acaba de conceder o negar: se vuelve a leer la lista. */
    data object PermissionAnswered : ContactPickerUiEvent

    data class Choose(val contact: DeviceContact) : ContactPickerUiEvent
}
