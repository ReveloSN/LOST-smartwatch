package com.lost.data.contacts

import com.lost.domain.error.LostResult
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.ContactNotifier
import javax.inject.Inject
import kotlinx.coroutines.delay

/** Aviso simulado para la demo: no envía nada, solo espera como si enviara. */
class FakeContactNotifier @Inject constructor() : ContactNotifier {
    var lastShared: Pair<AuthorizedContact, GeoPoint>? = null
        private set

    override suspend fun shareLocation(contact: AuthorizedContact, location: GeoPoint): LostResult<Unit> {
        delay(SIMULATED_SEND_MILLIS)
        lastShared = contact to location
        return LostResult.Success(Unit)
    }

    private companion object {
        const val SIMULATED_SEND_MILLIS = 800L
    }
}
