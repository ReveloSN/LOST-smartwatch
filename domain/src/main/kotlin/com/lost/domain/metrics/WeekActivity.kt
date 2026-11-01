package com.lost.domain.metrics

import java.time.DayOfWeek

/** Lo que pasó cada día de esta semana: en qué días hubo recorrido y en cuáles LOST notó un desvío. */
data class WeekActivity(val daysWithTrips: Set<DayOfWeek>, val daysWithDeviation: Set<DayOfWeek>) {
    companion object {
        val EMPTY = WeekActivity(emptySet(), emptySet())
    }
}
