package com.lost.domain.usecase

import com.lost.domain.error.LostResult
import com.lost.domain.model.DeviceContact
import com.lost.domain.port.DeviceContactsReader
import javax.inject.Inject

/** Los contactos del reloj que se pueden autorizar: con nombre y teléfono, sin repetir, por orden alfabético. */
class GetDeviceContactsUseCase @Inject constructor(private val reader: DeviceContactsReader) {
    suspend operator fun invoke(): LostResult<List<DeviceContact>> = when (val result = reader.contacts()) {
        is LostResult.Failure -> result

        is LostResult.Success -> LostResult.Success(
            result.value
                .filter { it.name.isNotBlank() && it.phoneNumber.isNotBlank() }
                .distinctBy { it.id }
                .sortedBy { it.name.lowercase() }
        )
    }
}
