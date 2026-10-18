package com.lost.ai.fixtures

import com.lost.domain.detection.DetectionRequest
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.Routine
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.Trip

/** Atajo para construir solicitudes al motor en las pruebas. */
object EngineRequests {
    fun request(
        trip: Trip,
        routines: List<Routine> = listOf(TripFixtures.casaU()),
        destination: GeoPoint? = null,
        thresholds: PersonalThresholds = PersonalThresholds.DEFAULT
    ) = DetectionRequest(trip, routines, destination, thresholds, Sensitivity.MEDIUM)
}
