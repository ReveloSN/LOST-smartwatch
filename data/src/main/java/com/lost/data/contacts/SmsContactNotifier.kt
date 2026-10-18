package com.lost.data.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.ContactNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Envía la ubicación por SMS. Si el reloj no puede enviar mensajes (sin plan de datos o sin permiso)
 * devuelve un error explícito: la UI dice "No pudimos avisar a Laura" y lo reintenta.
 */
class SmsContactNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val composer: LocationMessageComposer
) : ContactNotifier {
    override suspend fun shareLocation(contact: AuthorizedContact, location: GeoPoint): LostResult<Unit> {
        if (!canSendSms()) return LostResult.Failure(LostError.NoConnection)
        val smsManager = context.getSystemService(SmsManager::class.java)
            ?: return LostResult.Failure(LostError.NoConnection)
        return runCatching {
            smsManager.sendTextMessage(contact.phoneNumber, null, composer.compose(location), null, null)
        }.fold(
            onSuccess = { LostResult.Success(Unit) },
            onFailure = { LostResult.Failure(LostError.NoConnection) }
        )
    }

    private fun canSendSms(): Boolean {
        val hasTelephony = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_MESSAGING)
        val hasPermission =
            context.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        return hasTelephony && hasPermission
    }
}
