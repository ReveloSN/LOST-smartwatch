package com.lost.domain.port

import com.lost.domain.model.AlertPlace

/** Guarda los lugares donde LOST preguntó. */
interface AlertPlaceRecorder {
    suspend fun record(place: AlertPlace)

    suspend fun deleteAll()
}
