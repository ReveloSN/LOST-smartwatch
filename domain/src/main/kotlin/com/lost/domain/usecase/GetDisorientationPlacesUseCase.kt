package com.lost.domain.usecase

import com.lost.domain.metrics.AlertPlaceGrouper
import com.lost.domain.metrics.DisorientationPlace
import com.lost.domain.metrics.PlaceLabeler
import com.lost.domain.port.AlertPlaceReader
import com.lost.domain.port.DestinationReader
import com.lost.domain.port.TimeSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** 03.2 · Los lugares donde más te desorientas en los últimos 30 días, del más frecuente al menos. */
class GetDisorientationPlacesUseCase @Inject constructor(
    private val reader: AlertPlaceReader,
    private val destinations: DestinationReader,
    private val grouper: AlertPlaceGrouper,
    private val labeler: PlaceLabeler,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(): List<DisorientationPlace> {
        val since = timeSource.nowMillis() - TimeUnit.DAYS.toMillis(DAYS)
        val saved = destinations.observeDestinations().first()
        return grouper.group(reader.placesSince(since))
            .sortedWith(compareByDescending<List<*>> { it.size })
            .take(MAX_PLACES)
            .map { group ->
                DisorientationPlace(
                    center = group.first().location,
                    times = group.size,
                    mainCause = group.mapNotNull {
                        it.cause
                    }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key,
                    label = labeler.label(group, saved)
                )
            }
    }

    private companion object {
        const val DAYS = 30L
        const val MAX_PLACES = 3
    }
}
