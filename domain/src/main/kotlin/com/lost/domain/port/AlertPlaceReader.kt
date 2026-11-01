package com.lost.domain.port

import com.lost.domain.model.AlertPlace

/** Lee los lugares donde LOST preguntó. */
fun interface AlertPlaceReader {
    suspend fun placesSince(epochMillis: Long): List<AlertPlace>
}
