package com.lost.domain.port

import com.lost.domain.model.Trip

/** Lee recorridos guardados. */
interface TripReader {
    suspend fun tripsSince(epochMillis: Long): List<Trip>
}
