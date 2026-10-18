package com.lost.data.contacts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lost.data.di.ContactStore
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.port.ContactReader
import com.lost.domain.port.ContactWriter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Guarda el contacto autorizado (uno solo) en DataStore. Quitar permiso lo borra por completo. */
class AuthorizedContactRepository @Inject constructor(@ContactStore private val dataStore: DataStore<Preferences>) :
    ContactReader,
    ContactWriter {
    override fun observeContact(): Flow<AuthorizedContact?> = dataStore.data.map(::toContact)

    override suspend fun save(contact: AuthorizedContact) {
        dataStore.edit {
            it[ID] = contact.id
            it[NAME] = contact.name
            it[RELATIONSHIP] = contact.relationship
            it[PHONE] = contact.phoneNumber
            it[ACCEPTED] = contact.hasAcceptedInvitation
        }
    }

    override suspend fun revoke() {
        dataStore.edit { it.clear() }
    }

    private fun toContact(preferences: Preferences): AuthorizedContact? {
        val id = preferences[ID] ?: return null
        return AuthorizedContact(
            id = id,
            name = preferences[NAME].orEmpty(),
            relationship = preferences[RELATIONSHIP].orEmpty(),
            phoneNumber = preferences[PHONE].orEmpty(),
            hasAcceptedInvitation = preferences[ACCEPTED] ?: false
        )
    }

    private companion object {
        val ID = stringPreferencesKey("contact_id")
        val NAME = stringPreferencesKey("contact_name")
        val RELATIONSHIP = stringPreferencesKey("contact_relationship")
        val PHONE = stringPreferencesKey("contact_phone")
        val ACCEPTED = booleanPreferencesKey("contact_accepted")
    }
}
