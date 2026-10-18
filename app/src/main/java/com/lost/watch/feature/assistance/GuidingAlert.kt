package com.lost.watch.feature.assistance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.DistanceContent
import com.lost.designsystem.components.molecules.DistanceIndicator
import com.lost.designsystem.components.organisms.ProbabilityArc
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import com.lost.watch.ui.formatDistance
import kotlin.math.ceil

private const val WALKING_METERS_PER_MINUTE = 84.0
private const val GUIDE_ARC = 1f

/** F.3 · Volver a tu ruta de siempre: flecha grande, distancia y tiempo caminando. */
@Composable
fun GuidingAlert(state: AssistanceUiState.Guiding) {
    WatchScreenTemplate(ring = { ProbabilityArc(StatusTone.ON_ROUTE, GUIDE_ARC) }) {
        val title = if (state.destinationName !=
            null
        ) {
            state.destinationName
        } else {
            stringResource(R.string.assist_back_to_route)
        }
        LostText(title, role = LostTextRole.CAPTION)
        val meters = state.distanceMeters ?: 0.0
        val minutes = ceil(meters / WALKING_METERS_PER_MINUTE).toInt().coerceAtLeast(1)
        DistanceIndicator(
            DistanceContent(
                distance = formatDistance(meters),
                detail = pluralStringResource(R.plurals.assist_walk_minutes, minutes, minutes),
                arrowRotationDegrees = state.bearingDegrees
            )
        )
        val toward = state.destinationName?.let { stringResource(R.string.assist_toward_destination, it) }
            ?: stringResource(R.string.assist_toward_route)
        LostText(toward, role = LostTextRole.CAPTION)
    }
}

@LostDevicePreviews
@Composable
private fun GuidingPreview() {
    LostTheme { GuidingAlert(AssistanceUiState.Guiding(220.0, 45f, null)) }
}
