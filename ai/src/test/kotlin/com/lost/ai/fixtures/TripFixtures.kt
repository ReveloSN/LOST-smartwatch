package com.lost.ai.fixtures

import com.lost.ai.geometry.LocalProjection
import com.lost.ai.geometry.PlanePoint
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Recorridos sintéticos de los escenarios de la sección 8.5.
 * Rutina "Casa → U": 600 m al este y 500 m al norte (≈ 13 min caminando).
 */
object TripFixtures {
    val ORIGIN = GeoPoint(4.6097, -74.0817)
    val MONDAY_6_40: Long = ZonedDateTime.of(2026, 10, 12, 6, 40, 0, 0, ZoneOffset.UTC).toInstant().toEpochMilli()

    private val projection = LocalProjection(ORIGIN)

    fun at(x: Double, y: Double): GeoPoint = projection.toGeoPoint(PlanePoint(x, y))

    fun habitualWalk() = SyntheticWalk().to(600.0, 0.0).to(600.0, 500.0)

    fun casaU(): Routine = Routine(
        id = "casa-u",
        name = "Casa → U",
        destinationKind = PlaceKind.UNIVERSITY,
        path = TrajectoryResampler(20.0).resample(habitualWalk().points()),
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, departureMinuteOfDay = 400),
        duration = DurationStatistics(count = 5, meanMinutes = 13.1, m2 = 2.0),
        timesObserved = 5,
        isConfirmed = true
    )

    /** Escenario 1: va por su ruta de siempre (con un poco de ruido del GPS). */
    fun normalRoutineTrip() = SyntheticWalk()
        .to(200.0, 3.0).to(400.0, -3.0).to(600.0, 2.0).to(598.0, 200.0).to(602.0, 300.0)
        .trip()

    /** Escenario 2: se desvía a propósito y camina recto y estable hacia otro lugar. */
    fun voluntaryDetourTrip() = SyntheticWalk().to(300.0, 0.0).to(300.0, -400.0).trip()

    /** Escenario 3: se desvía, da 3 vueltas a la misma manzana, cambia de rumbo y se aleja. */
    fun disorientedTrip() = SyntheticWalk()
        .to(300.0, 0.0).to(300.0, -150.0)
        .square(side = 60.0, times = 3)
        .to(330.0, -120.0).to(280.0, -170.0)
        .to(300.0, -300.0)
        .trip()

    /** Escenario 4: hospital sin rutina, destino 300 m al norte; da vueltas y vuelve al ascensor. */
    fun newPlaceLoopsTrip() = SyntheticWalk()
        .to(0.0, 60.0).to(40.0, 60.0).to(40.0, 20.0).to(0.0, 20.0)
        .to(0.0, 60.0).to(40.0, 60.0).to(40.0, 20.0).to(0.0, 20.0)
        .to(0.0, 60.0).to(40.0, 60.0).to(40.0, 20.0).to(0.0, 20.0)
        .to(0.0, 0.0)
        .trip()

    val HOSPITAL_ROOM = at(0.0, 300.0)
}
