package com.lost.domain.usecase

import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.DeviceContact
import javax.inject.Inject

/**
 * Autoriza a un contacto del reloj: recibirá la ubicación por SMS solo cuando LOST ayude. No hay invitación que
 * aceptar (no existe una app del contacto), así que queda autorizado al elegirlo; el parentesco queda vacío.
 */
class AuthorizeDeviceContactUseCase @Inject constructor(private val choose: ChooseAuthorizedContactUseCase) {
    suspend operator fun invoke(contact: DeviceContact) = choose(
        AuthorizedContact(
            id = ID_PREFIX + contact.id,
            name = contact.name,
            relationship = "",
            phoneNumber = contact.phoneNumber,
            hasAcceptedInvitation = true
        )
    )

    private companion object {
        const val ID_PREFIX = "device-"
    }
}
