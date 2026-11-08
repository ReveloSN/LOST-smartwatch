package com.lost.ai.sequence

import com.lost.ai.fixtures.SyntheticWalk
import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Retomar las filas del DTW punto a punto da exactamente lo mismo que rehacer la matriz cada vez. */
class DtwIncrementalTest {
    private val reference = SyntheticWalk().to(600.0, 0.0).to(600.0, 500.0).points()
    private val trip = SyntheticWalk().to(300.0, 0.0).to(300.0, -150.0).square(side = 60.0, times = 3)
        .to(600.0, 0.0).to(600.0, 400.0).points()

    private fun comparator() = DtwSequenceComparator(BAND_RATIO, MIN_BAND)

    @Test
    fun `seguir el recorrido punto a punto da lo mismo que compararlo de cero`() {
        val incremental = comparator()
        (1..trip.size).forEach { n ->
            val prefix = trip.take(n)
            assertEquals(comparator().compare(reference, prefix, true), incremental.compare(reference, prefix, true))
        }
    }

    @Test
    fun `si el ultimo punto provisional cambia se recalcula bien`() {
        val incremental = comparator()
        (2..trip.size).forEach { n ->
            val provisional = trip.take(n - 1) + TripFixtures.at(n * 3.0, -n * 2.0)
            incremental.compare(reference, provisional, true)
            val settled = trip.take(n)
            assertEquals(comparator().compare(reference, settled, true), incremental.compare(reference, settled, true))
        }
    }

    @Test
    fun `otro recorrido o varias rutinas no se mezclan`() {
        val incremental = comparator()
        val otherRoute = SyntheticWalk().to(0.0, 400.0).points()
        val otherTrip: List<GeoPoint> = SyntheticWalk().to(-200.0, 0.0).points()
        listOf(
            reference to trip,
            otherRoute to trip,
            reference to otherTrip,
            otherRoute to otherTrip
        ).forEach { (route, walk) ->
            (1..walk.size step STEP).forEach { n ->
                val prefix = walk.take(n)
                assertEquals(comparator().compare(route, prefix, true), incremental.compare(route, prefix, true))
                assertEquals(comparator().compare(route, prefix, false), incremental.compare(route, prefix, false))
            }
        }
    }

    private companion object {
        const val BAND_RATIO = 0.25
        const val MIN_BAND = 5
        const val STEP = 7
    }
}
