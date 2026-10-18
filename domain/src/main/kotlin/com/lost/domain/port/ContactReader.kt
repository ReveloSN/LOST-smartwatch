package com.lost.domain.port

import com.lost.domain.model.AuthorizedContact
import kotlinx.coroutines.flow.Flow

/** Lectura del contacto autorizado. */
fun interface ContactReader {
    fun observeContact(): Flow<AuthorizedContact?>
}
