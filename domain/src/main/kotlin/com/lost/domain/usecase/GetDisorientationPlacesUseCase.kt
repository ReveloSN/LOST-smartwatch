package com.lost.domain.usecase

import com.lost.domain.metrics.DisorientationPlace
import com.lost.domain.metrics.DisorientationPlaceSummarizer
import com.lost.domain.metrics.InsightsThresholds
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
    private val summarizer: DisorientationPlaceSummarizer,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(): List<DisorientationPlace> {
        val since = timeSource.nowMillis() - TimeUnit.DAYS.toMillis(InsightsThresholds.PLACES_WINDOW_DAYS)
        return summarizer.summarize(reader.placesSince(since), destinations.observeDestinations().first())
    }
}
