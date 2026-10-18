package com.lost.domain.port

import com.lost.domain.model.Trip

/** Guarda recorridos terminados para que la IA aprenda de ellos. */
interface TripRecorder {
    suspend fun record(trip: Trip)

    suspend fun deleteAll()
}
