package com.lost.domain.usecase

import com.lost.domain.model.Destination
import com.lost.domain.port.DestinationWriter
import com.lost.domain.port.TimeSource
import javax.inject.Inject

/** "Guardar como destino" o marcar un destino como usado ahora. */
class SaveDestinationUseCase @Inject constructor(
    private val writer: DestinationWriter,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(destination: Destination) =
        writer.save(destination.copy(lastUsedAtMillis = timeSource.nowMillis()))
}
