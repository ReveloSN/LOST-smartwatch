package com.lost.data.persistence

import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule

object DataFixtures {
    fun routine(id: String = "r1") = Routine(
        id = id,
        name = "Casa → U",
        destinationKind = PlaceKind.UNIVERSITY,
        path = listOf(GeoPoint(4.6, -74.0, 1L), GeoPoint(4.61, -74.0, 2L)),
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
        duration = DurationStatistics(5, 35.0, 12.5),
        timesObserved = 5,
        isConfirmed = true
    )
}
