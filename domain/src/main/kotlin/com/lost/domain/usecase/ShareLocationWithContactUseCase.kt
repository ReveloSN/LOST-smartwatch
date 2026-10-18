package com.lost.domain.usecase

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.ContactNotifier
import com.lost.domain.port.ContactReader
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** Comparte la ubicación con el contacto autorizado. Falla de forma explícita si no hay contacto. */
class ShareLocationWithContactUseCase @Inject constructor(
    private val contactReader: ContactReader,
    private val contactNotifier: ContactNotifier,
    private val trackMetric: TrackMetricEventUseCase
) {
    suspend operator fun invoke(location: GeoPoint): LostResult<AuthorizedContact> {
        val contact = contactReader.observeContact().first()
            ?: return LostResult.Failure(LostError.NoAuthorizedContact)
        return when (val sent = contactNotifier.shareLocation(contact, location)) {
            is LostResult.Failure -> sent

            is LostResult.Success -> {
                trackMetric(MetricEventType.LOCATION_SHARED)
                LostResult.Success(contact)
            }
        }
    }
}
