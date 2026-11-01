package com.lost.domain.metrics

import com.lost.domain.model.AlertPlace
import com.lost.domain.model.Destination
import com.lost.domain.port.GuidanceCalculator
import javax.inject.Inject

/**
 * Nombra un lugar con lo que LOST ya sabe, sin geocodificación inversa: el destino guardado más cercano o la
 * rutina que se recorría. Así la ubicación de los momentos difíciles nunca sale del reloj.
 */
class PlaceLabeler @Inject constructor(private val distances: GuidanceCalculator) {
    fun label(group: List<AlertPlace>, destinations: List<Destination>): PlaceLabel {
        val center = group.first().location
        val nearest = destinations
            .mapNotNull { destination ->
                distances.guide(center, emptyList(), destination.location)?.let {
                    destination to
                        it
                }
            }
            .minByOrNull { (_, guidance) -> guidance.distance.value }
            ?.takeIf { (_, guidance) -> guidance.distance.value <= InsightsThresholds.NEAR_DESTINATION_METERS }
            ?.first
        val routine = group.mapNotNull { it.routineName }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        return when {
            nearest != null -> PlaceLabel.NearDestination(nearest.name)
            routine != null -> PlaceLabel.OnRoutine(routine)
            else -> PlaceLabel.Unknown
        }
    }
}
