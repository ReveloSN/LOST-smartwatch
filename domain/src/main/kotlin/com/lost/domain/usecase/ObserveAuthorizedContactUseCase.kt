package com.lost.domain.usecase

import com.lost.domain.model.AuthorizedContact
import com.lost.domain.port.ContactReader
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Contacto autorizado actual, o `null` si no hay. */
class ObserveAuthorizedContactUseCase @Inject constructor(private val reader: ContactReader) {
    operator fun invoke(): Flow<AuthorizedContact?> = reader.observeContact()
}
