package com.lost.data.demo

import android.content.Context
import com.lost.data.BuildConfig
import com.lost.data.R
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.Destination
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * En modo demo deja lista la historia de Sebastián: la rutina "Casa → U" ya aprendida
 * (a partir del GPX normal), a Laura como contacto y algunos destinos.
 */
class DemoDataSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repositories: DemoRepositories,
    private val opener: GpxAssetOpener,
    private val reader: GpxRouteReader
) {
    suspend fun seedIfNeeded() {
        if (!BuildConfig.DEMO_MODE) return
        if (repositories.routineReader.routines().isEmpty()) repositories.routineWriter.save(habitualRoutine())
        if (repositories.contactReader.observeContact().first() == null) repositories.contactWriter.save(laura())
        demoDestinations().forEach { repositories.destinationWriter.save(it) }
    }

    private fun habitualRoutine(): Routine {
        val path = opener.open(DemoScenario.NORMAL_ROUTINE.assetName).use(reader::read)
        return Routine(
            id = HABITUAL_ROUTINE_ID,
            name = context.getString(R.string.demo_routine_name),
            destinationKind = PlaceKind.UNIVERSITY,
            path = path,
            schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, DEPARTURE_6_40),
            duration = DurationStatistics(count = 5, meanMinutes = minutesOf(path), m2 = 2.0),
            timesObserved = 5,
            isConfirmed = true
        )
    }

    /** La duración habitual sale del GPX: si se regeneran las rutas, la demo sigue cuadrando. */
    private fun minutesOf(path: List<GeoPoint>): Double =
        (path.last().timestampMillis - path.first().timestampMillis) / MILLIS_PER_MINUTE

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

    private fun arrivalOf(scenario: DemoScenario): GeoPoint = opener.open(scenario.assetName).use(reader::read).last()

    private companion object {
        const val HABITUAL_ROUTINE_ID = "demo-casa-u"
        const val CONTACT_ID = "demo-laura"
        const val DEPARTURE_6_40 = 400
        const val MILLIS_PER_MINUTE = 60_000.0
    }
}
