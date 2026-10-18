package com.lost.domain.usecase

import com.lost.domain.model.AuthorizedContact
import com.lost.domain.port.ContactWriter
import javax.inject.Inject

/** Elige al contacto autorizado. Queda pendiente hasta que acepte la invitación. */
class ChooseAuthorizedContactUseCase @Inject constructor(private val writer: ContactWriter) {
    suspend operator fun invoke(contact: AuthorizedContact) = writer.save(contact)
}
