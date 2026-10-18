package com.lost.watch.integration

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.error.LostResult
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.ShareLocationWithContactUseCase
import javax.inject.Inject
import kotlinx.coroutines.delay

/**
 * Cuando la cuenta regresiva termina, avisa al contacto. Si no hay conexión lo reintenta
 * cada 20 s (lámina ERR.2) mientras siga en "Aviso enviado".
 */
class LocationSharingEffect @Inject constructor(
    private val session: TripSession,
    private val shareLocation: ShareLocationWithContactUseCase,
    private val outcome: ShareOutcomeStore
) {
    suspend fun onTransition(transition: AssistanceTransition) {
        if (transition.to != AssistanceState.LocationShared || transition.from == AssistanceState.LocationShared) return
        var attempt = 0
        while (attempt < MAX_ATTEMPTS && isStillSharing() && !tryShare()) {
            attempt++
            delay(RETRY_MILLIS)
        }
    }

    private fun isStillSharing() = session.snapshot.value.assistance == AssistanceState.LocationShared

    /** Devuelve `true` si ya no hay que reintentar (se envió o no hay ubicación). */
    private suspend fun tryShare(): Boolean {
        val location = session.snapshot.value.currentLocation ?: return true
        val result = shareLocation(location)
        outcome.report(result.errorOrNull())
        return result is LostResult.Success
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val RETRY_MILLIS = 20_000L
    }
}
