package com.lost.domain.port

import com.lost.domain.model.AuthorizedContact

/** Guarda o revoca el contacto autorizado. */
interface ContactWriter {
    suspend fun save(contact: AuthorizedContact)

    suspend fun revoke()
}
