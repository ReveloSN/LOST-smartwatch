package com.lost.domain.usecase

import com.lost.domain.model.Destination
import com.lost.domain.port.DestinationReader
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Destinos ordenados del más reciente al más antiguo; los nunca usados al final. */
class ObserveDestinationsUseCase @Inject constructor(private val reader: DestinationReader) {
    operator fun invoke(): Flow<List<Destination>> = reader.observeDestinations().map { destinations ->
        destinations.sortedByDescending { it.lastUsedAtMillis ?: Long.MIN_VALUE }
    }
}
