package com.lost.domain.usecase

import com.lost.domain.port.ContactWriter
import javax.inject.Inject

/** "Quitar permiso": el contacto deja de recibir la ubicación. */
class RevokeAuthorizedContactUseCase @Inject constructor(private val writer: ContactWriter) {
    suspend operator fun invoke() = writer.revoke()
}
