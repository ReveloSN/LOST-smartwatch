package com.lost.domain.port

import com.lost.domain.error.LostResult
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint

/** Envía la ubicación al contacto autorizado con un mensaje calmado. */
interface ContactNotifier {
    suspend fun shareLocation(contact: AuthorizedContact, location: GeoPoint): LostResult<Unit>
}
