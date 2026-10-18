package com.lost.watch

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionResult
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Meters
import com.lost.domain.model.units.Probability
import com.lost.domain.port.ContactReader
import com.lost.domain.session.TripSessionSnapshot
import java.time.ZoneOffset
import java.time.ZonedDateTime

object AppFixtures {
    val START_6_40: Long = ZonedDateTime.of(2026, 10, 12, 6, 40, 0, 0, ZoneOffset.UTC).toInstant().toEpochMilli()

    val routine = Routine(
        id = "casa-u",
        name = "Casa → U",
        destinationKind = PlaceKind.UNIVERSITY,
        path = listOf(GeoPoint(4.6097, -74.0817), GeoPoint(4.6097, -74.0763), GeoPoint(4.6142, -74.0763)),
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
        duration = DurationStatistics(5, 35.0, 2.0),
        timesObserved = 5,
        isConfirmed = true
    )

    val trip =
        Trip("t", listOf(GeoPoint(4.6097, -74.0817, START_6_40), GeoPoint(4.6097, -74.0790, START_6_40 + 600_000)))

    val reasons = listOf(DeviationReason.LeftUsualRoute(Meters(150.0)), DeviationReason.RepeatedArea(3))

    fun detection(level: AssistanceLevel, reasons: List<DeviationReason> = emptyList(), voluntary: Boolean = false) =
        DetectionResult(Probability(0.3), level, reasons, voluntary, DetectionModeKind.ROUTINE, routine.id, 0.3)

    fun monitoring(detection: DetectionResult, assistance: AssistanceState = AssistanceState.Calm) =
        TripSessionSnapshot.IDLE.copy(
            isMonitoring = true,
            trip = trip,
            detection = detection,
            routine = routine,
            assistance = assistance
        )
}
