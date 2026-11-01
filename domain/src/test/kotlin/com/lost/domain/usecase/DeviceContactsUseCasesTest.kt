package com.lost.domain.usecase

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.DeviceContact
import com.lost.domain.port.ContactWriter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeviceContactsUseCasesTest {
    @Test
    fun `lista solo contactos con nombre y telefono sin repetir y por orden alfabetico`() = runTest {
        val raw = listOf(
            DeviceContact("2", "papá", "+57 301"),
            DeviceContact("1", "Laura", "+57 300"),
            DeviceContact("1", "Laura", "+57 300 (otro número)"),
            DeviceContact("3", "", "+57 302"),
            DeviceContact("4", "Sin número", "")
        )
        val result = GetDeviceContactsUseCase { LostResult.Success(raw) }()
        assertEquals(listOf("Laura", "papá"), result.valueOrNull()?.map { it.name })
    }

    @Test
    fun `sin permiso lo dice con un error conocido`() = runTest {
        val result = GetDeviceContactsUseCase { LostResult.Failure(LostError.ContactsPermissionDenied) }()
        assertEquals(LostError.ContactsPermissionDenied, result.errorOrNull())
    }

    @Test
    fun `autorizar un contacto del reloj lo guarda listo para recibir el aviso`() = runTest {
        var saved: AuthorizedContact? = null
        val writer = object : ContactWriter {
            override suspend fun save(contact: AuthorizedContact) {
                saved = contact
            }

            override suspend fun revoke() = Unit
        }
        AuthorizeDeviceContactUseCase(ChooseAuthorizedContactUseCase(writer))(DeviceContact("7", "Laura", "+57 300"))
        assertEquals(AuthorizedContact("device-7", "Laura", "", "+57 300", hasAcceptedInvitation = true), saved)
    }
}
