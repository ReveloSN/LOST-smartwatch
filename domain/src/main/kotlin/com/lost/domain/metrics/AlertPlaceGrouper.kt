package com.lost.domain.metrics

import com.lost.domain.model.AlertPlace
import com.lost.domain.port.GuidanceCalculator
import javax.inject.Inject

/**
 * Junta las alertas que ocurrieron cerca (a menos de una cuadra) en un mismo lugar. Con
 * pocas alertas por semana basta un agrupamiento voraz: cada alerta se suma al primer grupo cercano.
 */
class AlertPlaceGrouper @Inject constructor(private val distances: GuidanceCalculator) {
    fun group(places: List<AlertPlace>): List<List<AlertPlace>> {
        val groups = mutableListOf<MutableList<AlertPlace>>()
        places.sortedBy { it.timestampMillis }.forEach { place ->
            val near = groups.firstOrNull { isNear(it.first(), place) }
            if (near != null) near += place else groups += mutableListOf(place)
        }
        return groups
    }

    private fun isNear(anchor: AlertPlace, place: AlertPlace): Boolean =
        distances.guide(place.location, emptyList(), anchor.location)?.let {
            it.distance.value <=
                InsightsThresholds.ALERT_PLACE_RADIUS_METERS
        } ==
            true
}
