package com.lost.data.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.lost.data.di.IoDispatcher
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.DeviceContact
import com.lost.domain.port.DeviceContactsReader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Patrón Adapter: lee los contactos con teléfono del proveedor de contactos del reloj. Solo se leen para mostrar
 * la lista; LOST guarda únicamente al contacto que la persona elige.
 */
class AndroidDeviceContactsReader @Inject constructor(
    @ApplicationContext private val context: Context,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : DeviceContactsReader {
    override suspend fun contacts(): LostResult<List<DeviceContact>> = withContext(io) {
        if (!hasPermission()) return@withContext LostResult.Failure(LostError.ContactsPermissionDenied)
        val cursor = context.contentResolver.query(Phone.CONTENT_URI, PROJECTION, null, null, SORT)
        LostResult.Success(cursor?.use(::readAll).orEmpty())
    }

    private fun readAll(cursor: Cursor): List<DeviceContact> = buildList {
        while (cursor.moveToNext() && size < MAX_CONTACTS) {
            add(
                DeviceContact(
                    id = cursor.getString(0),
                    name = cursor.getString(1).orEmpty(),
                    phoneNumber = cursor.getString(2).orEmpty()
                )
            )
        }
    }

    private fun hasPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    private companion object {
        val PROJECTION = arrayOf(Phone.CONTACT_ID, Phone.DISPLAY_NAME_PRIMARY, Phone.NUMBER)
        const val SORT = "${Phone.DISPLAY_NAME_PRIMARY} ASC"
        const val MAX_CONTACTS = 200
    }
}
