package com.lost.ai.mode

import com.lost.ai.config.DetectionConfig
import com.lost.ai.learning.RoutineMatcher
import com.lost.ai.scoring.CompositeAnomalyScorer
import com.lost.ai.sequence.RouteProgressLocator
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.model.GeoPoint

/** Fábrica: si el recorrido pertenece a una rutina conocida usa el modo rutina; si no, el de lugar nuevo. */
class DetectionModeFactory(
    private val matcher: RoutineMatcher,
    private val scorer: CompositeAnomalyScorer,
    private val config: DetectionConfig,
    private val progressLocator: RouteProgressLocator = RouteProgressLocator()
) {
    fun create(path: List<GeoPoint>, request: DetectionRequest): DetectionMode {
        val match = matcher.bestOngoingMatch(path, request.routines)
        return if (match != null) {
            RoutineDetectionMode(match, scorer, config.routineWeights, progressLocator)
        } else {
            NewPlaceDetectionMode(scorer, config.newPlaceWeights)
        }
    }
}
