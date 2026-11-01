package com.lost.domain.metrics

import com.lost.domain.detection.DeviationCause
import com.lost.domain.model.AlertPlace
import com.lost.domain.model.Destination
import javax.inject.Inject

/** Convierte alertas sueltas en los lugares de 03.2: agrupa, ordena del más frecuente al menos y nombra. */
class DisorientationPlaceSummarizer @Inject constructor(
    private val grouper: AlertPlaceGrouper,
    private val labeler: PlaceLabeler
) {
    fun summarize(alerts: List<AlertPlace>, destinations: List<Destination>): List<DisorientationPlace> =
        grouper.group(alerts)
            .sortedByDescending { it.size }
            .take(InsightsThresholds.MAX_PLACES)
            .map { group ->
                DisorientationPlace(
                    center = group.first().location,
                    times = group.size,
                    mainCause = mostFrequentCause(group),
                    label = labeler.label(group, destinations)
                )
            }

    private fun mostFrequentCause(group: List<AlertPlace>): DeviationCause? =
        group.mapNotNull { it.cause }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
}
