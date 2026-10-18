package com.lost.domain.port

import com.lost.domain.model.Destination
import kotlinx.coroutines.flow.Flow

/** Lectura de destinos guardados y recientes. */
interface DestinationReader {
    fun observeDestinations(): Flow<List<Destination>>
}
