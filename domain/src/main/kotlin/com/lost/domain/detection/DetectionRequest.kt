package com.lost.domain.detection

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.Routine
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.Trip

/** Todo lo que el motor necesita para evaluar un recorrido en curso. */
data class DetectionRequest(
    val trip: Trip,
    val routines: List<Routine>,
    val destination: GeoPoint?,
    val thresholds: PersonalThresholds,
    val sensitivity: Sensitivity
)
