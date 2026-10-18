package com.lost.data.contacts

import com.lost.data.BuildConfig
import com.lost.domain.error.LostResult
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.ContactNotifier
import javax.inject.Inject

/** En modo demo avisa con [FakeContactNotifier]; en producción, con SMS. */
class ActiveContactNotifier @Inject constructor(
    private val sms: SmsContactNotifier,
    private val fake: FakeContactNotifier
) : ContactNotifier {
    override suspend fun shareLocation(contact: AuthorizedContact, location: GeoPoint): LostResult<Unit> =
        if (BuildConfig.DEMO_MODE) fake.shareLocation(contact, location) else sms.shareLocation(contact, location)
}
