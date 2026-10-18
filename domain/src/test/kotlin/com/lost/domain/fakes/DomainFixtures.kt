package com.lost.domain.fakes

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionResult
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Meters
import com.lost.domain.model.units.Probability

object DomainFixtures {
    val laura = AuthorizedContact("c1", "Laura", "Hermana", "+573000000000", hasAcceptedInvitation = true)

    val reasons = listOf(DeviationReason.LeftUsualRoute(Meters(120.0)), DeviationReason.RepeatedArea(3))

    fun result(level: AssistanceLevel, isVoluntary: Boolean = false) = DetectionResult(
        probability = Probability(
            when (level) {
                AssistanceLevel.LOW -> 0.2
                AssistanceLevel.MEDIUM -> 0.6
                AssistanceLevel.HIGH -> 0.9
            }
        ),
        level = level,
        reasons = reasons,
        isVoluntaryDetour = isVoluntary,
        mode = DetectionModeKind.ROUTINE,
        routineId = "r1",
        routeProgress = 0.4
    )

    fun trip(points: Int = 5) = Trip(
        id = "t1",
        points = List(points) { GeoPoint(4.60 + it * 0.0001, -74.08, it * 10_000L) }
    )

    fun routine(id: String = "r1", isConfirmed: Boolean = true, departure: Int = 400) = Routine(
        id = id,
        name = "Casa → U",
        destinationKind = PlaceKind.UNIVERSITY,
        path = trip().points,
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, departure),
        duration = DurationStatistics(5, 35.0, 10.0),
        timesObserved = 5,
        isConfirmed = isConfirmed
    )
}
