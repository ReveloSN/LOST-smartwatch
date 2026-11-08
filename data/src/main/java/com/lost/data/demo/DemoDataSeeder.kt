package com.lost.data.demo

import android.content.Context
import com.lost.data.BuildConfig
import com.lost.data.R
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * En modo demo deja listos a Laura como contacto y algunos destinos. La rutina "Casa → U" no se siembra:
 * la demo empieza como la vería una persona nueva y la aprende a la vista (`DemoRoutineLearning`).
 */
class DemoDataSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repositories: DemoRepositories,
    private val gpx: DemoGpxLoader
) {
    suspend fun seedIfNeeded() {
        if (!BuildConfig.DEMO_MODE) return
        if (repositories.contactReader.observeContact().first() == null) repositories.contactWriter.save(laura())
        demoDestinations().forEach { repositories.destinationWriter.save(it) }
    }

    private fun laura() = AuthorizedContact(
        id = CONTACT_ID,
        name = context.getString(R.string.demo_contact_name),
        relationship = context.getString(R.string.demo_contact_relationship),
        phoneNumber = "",
        hasAcceptedInvitation = true
    )

    private fun demoDestinations(): List<Destination> = DemoDestinations.all(
        context,
        campus = arrivalOf(DemoScenario.NORMAL_ROUTINE),
        clinic = arrivalOf(DemoScenario.NEW_PLACE)
    )

    private fun arrivalOf(scenario: DemoScenario): GeoPoint = gpx.points(scenario).last()

    private companion object {
        const val CONTACT_ID = "demo-laura"
    }
}
