package com.lost.domain.port

import com.lost.domain.model.Destination

/** Guarda un destino o marca que se usó. */
interface DestinationWriter {
    suspend fun save(destination: Destination)
}
