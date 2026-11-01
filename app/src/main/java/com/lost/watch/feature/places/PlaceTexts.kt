package com.lost.watch.feature.places

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.AltRoute
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.PanTool
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.domain.detection.DeviationCause
import com.lost.domain.metrics.DisorientationPlace
import com.lost.domain.metrics.PlaceLabel
import com.lost.watch.R

/** Nombre humano de un lugar: nunca coordenadas. */
@Composable
fun placeName(label: PlaceLabel): String = when (label) {
    is PlaceLabel.NearDestination -> stringResource(R.string.places_near, label.name)
    is PlaceLabel.OnRoutine -> stringResource(R.string.places_on_routine, label.name)
    PlaceLabel.Unknown -> stringResource(R.string.places_unknown)
}

/** "2 veces · diste vueltas", o solo "2 veces" si no se conoce la causa. */
@Composable
fun placeDetail(place: DisorientationPlace): String {
    val times = pluralStringResource(R.plurals.places_times, place.times, place.times)
    val cause = place.mainCause ?: return times
    return stringResource(R.string.places_times_with_cause, times, stringResource(causeText(cause)))
}

private fun causeText(cause: DeviationCause): Int = when (cause) {
    DeviationCause.LEFT_ROUTE -> R.string.places_cause_left_route
    DeviationCause.REPEATED_AREA -> R.string.places_cause_repeated
    DeviationCause.TAKING_LONGER -> R.string.places_cause_longer
    DeviationCause.MOVING_AWAY -> R.string.places_cause_moving_away
    DeviationCause.ERRATIC_DIRECTION -> R.string.places_cause_erratic
    DeviationCause.UNUSUAL_STOP -> R.string.places_cause_stop
}

/** Icono de la causa; siempre va acompañado del texto. */
fun causeIcon(cause: DeviationCause?): ImageVector = when (cause) {
    DeviationCause.LEFT_ROUTE -> Icons.Rounded.AltRoute
    DeviationCause.REPEATED_AREA -> Icons.Rounded.Replay
    DeviationCause.TAKING_LONGER -> Icons.Rounded.Schedule
    DeviationCause.MOVING_AWAY -> Icons.Rounded.Explore
    DeviationCause.ERRATIC_DIRECTION -> Icons.Rounded.Shuffle
    DeviationCause.UNUSUAL_STOP -> Icons.Rounded.PanTool
    null -> Icons.AutoMirrored.Rounded.Help
}
