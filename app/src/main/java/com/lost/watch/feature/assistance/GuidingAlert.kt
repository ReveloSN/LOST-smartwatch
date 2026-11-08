package com.lost.watch.feature.assistance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.molecules.DistanceContent
import com.lost.designsystem.components.molecules.DistanceIndicator
import com.lost.designsystem.components.organisms.ProbabilityArc
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import com.lost.watch.ui.arrowDescription
import com.lost.watch.ui.formatDistance
import kotlin.math.ceil

private const val WALKING_METERS_PER_MINUTE = 84.0
private const val GUIDE_ARC = 1f

/**
 * F.3 · Volver a tu ruta: a dónde (título), flecha con la distancia y el tiempo caminando, y una
 * salida ("Ya sé dónde estoy") para no quedar atrapado en la guía si no vuelve a la ruta.
 */
@Composable
fun GuidingAlert(state: AssistanceUiState.Guiding, onEvent: (AssistanceUiEvent) -> Unit) {
    WatchScreenTemplate(ring = { ProbabilityArc(StatusTone.ON_ROUTE, GUIDE_ARC) }) {
        LostText(state.destinationName ?: stringResource(R.string.assist_back_to_route), role = LostTextRole.TITLE)
        val meters = state.distanceMeters ?: 0.0
        val minutes = ceil(meters / WALKING_METERS_PER_MINUTE).toInt().coerceAtLeast(1)
        DistanceIndicator(
            DistanceContent(
                distance = formatDistance(meters),
                detail = pluralStringResource(R.plurals.assist_walk_minutes, minutes, minutes),
                arrowRotationDegrees = state.arrowDegrees,
                arrowDescription = arrowDescription(state.bearingDegrees)
            )
        )
        // Siempre hay una salida: si ya sabe dónde está, deja la guía (cuenta como "Estoy bien").
        LostSecondaryButton(
            stringResource(R.string.assist_end_guidance),
            { onEvent(AssistanceUiEvent.EndGuidance) },
            style = SecondaryButtonStyle.TEXT_ONLY
        )
    }
}

@LostDevicePreviews
@Composable
private fun GuidingPreview() {
    LostTheme { GuidingAlert(AssistanceUiState.Guiding(220.0, 45f, null)) {} }
}
