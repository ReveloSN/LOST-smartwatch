package com.lost.domain.port

import com.lost.domain.error.LostResult
import com.lost.domain.model.DeviceContact

/** Lee los contactos del reloj con teléfono. Sin permiso devuelve `LostError.ContactsPermissionDenied`. */
fun interface DeviceContactsReader {
    suspend fun contacts(): LostResult<List<DeviceContact>>
}
