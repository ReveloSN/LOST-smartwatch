package com.lost.watch.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AltRoute
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.UTurnLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.molecules.ReasonContent
import com.lost.domain.detection.DeviationReason
import com.lost.watch.R

/** Convierte una razón de la IA en icono + dato fuerte + contexto, en el idioma del usuario. */
@Composable
fun reasonContent(reason: DeviationReason): ReasonContent =
    ReasonContent(reasonIcon(reason), reasonHeadline(reason), reasonDetail(reason))

/** Icono de cada razón (lámina "Anomalías"). */
fun reasonIcon(reason: DeviationReason): ImageVector = when (reason) {
    is DeviationReason.LeftUsualRoute -> Icons.Rounded.AltRoute
    is DeviationReason.RepeatedArea -> Icons.Rounded.Cached
    is DeviationReason.TakingLonger -> Icons.Rounded.Timer
    DeviationReason.MovingAwayFromDestination -> Icons.Rounded.NorthEast
    DeviationReason.ErraticDirection -> Icons.Rounded.UTurnLeft
    DeviationReason.UnusualStop -> Icons.Rounded.Gesture
}

@Composable
private fun reasonHeadline(reason: DeviationReason): String = when (reason) {
    is DeviationReason.LeftUsualRoute -> stringResource(R.string.reason_left_route_headline)

    is DeviationReason.RepeatedArea -> pluralStringResource(
        R.plurals.reason_repeated_headline,
        reason.times,
        reason.times
    )

    is DeviationReason.TakingLonger ->
        pluralStringResource(R.plurals.reason_taking_longer_headline, reason.extraMinutes, reason.extraMinutes)

    DeviationReason.MovingAwayFromDestination -> stringResource(R.string.reason_moving_away_headline)

    DeviationReason.ErraticDirection -> stringResource(R.string.reason_erratic_headline)

    DeviationReason.UnusualStop -> stringResource(R.string.reason_unusual_stop_headline)
}

@Composable
private fun reasonDetail(reason: DeviationReason): String = when (reason) {
    is DeviationReason.LeftUsualRoute ->
        stringResource(R.string.reason_left_route_detail, formatDistance(reason.distance.value))

    is DeviationReason.RepeatedArea -> stringResource(R.string.reason_repeated_detail)

    is DeviationReason.TakingLonger -> stringResource(R.string.reason_taking_longer_detail)

    DeviationReason.MovingAwayFromDestination -> stringResource(R.string.reason_moving_away_detail)

    DeviationReason.ErraticDirection -> stringResource(R.string.reason_erratic_detail)

    DeviationReason.UnusualStop -> stringResource(R.string.reason_unusual_stop_detail)
}
