package com.lost.domain.usecase

import com.lost.domain.detection.DetectionRequest
import com.lost.domain.detection.DetectionResult
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.SettingsReader
import com.lost.domain.port.TripEvaluator
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** Evalúa el recorrido en curso contra las rutinas conocidas y los umbrales del usuario. */
class EvaluateCurrentTripUseCase @Inject constructor(
    private val evaluator: TripEvaluator,
    private val routineReader: RoutineReader,
    private val settingsReader: SettingsReader
) {
    suspend operator fun invoke(trip: Trip, destination: GeoPoint? = null): DetectionResult {
        if (!trip.hasEnoughPoints) return DetectionResult.CALM
        val settings = settingsReader.observeSettings().first()
        val request = DetectionRequest(
            trip = trip,
            routines = routineReader.routines(),
            destination = destination,
            thresholds = settings.thresholds,
            sensitivity = settings.sensitivity
        )
        return evaluator.evaluate(request)
    }
}
