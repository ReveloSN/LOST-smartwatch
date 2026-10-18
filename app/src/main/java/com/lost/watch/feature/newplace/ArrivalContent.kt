package com.lost.watch.feature.newplace

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.organisms.BreathingStatusRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R

private const val FULL_RING = 1f

/** 02.4 · Llegada: anillo completo, "Llegaste a la Biblioteca" y opción de guardar el destino. */
@Composable
fun ArrivalContent(destinationName: String?, onSave: () -> Unit, onDone: () -> Unit) {
    WatchScreenTemplate(ring = { BreathingStatusRing(StatusTone.ON_ROUTE, FULL_RING) }) {
        LostIcon(
            Icons.Rounded.CheckCircle,
            null,
            tint = SemanticTokens.Status.onRoute,
            size = ComponentTokens.Icon.large
        )
        val title =
            destinationName?.let { stringResource(R.string.newplace_arrived, it) }
                ?: stringResource(R.string.assist_arrived)
        LostText(title, role = LostTextRole.QUESTION)
        LostPrimaryButton(stringResource(R.string.newplace_save_destination), onSave)
        LostSecondaryButton(
            stringResource(R.string.onboarding_continue),
            onDone,
            style = SecondaryButtonStyle.TEXT_ONLY
        )
    }
}

@LostDevicePreviews
@Composable
private fun ArrivalPreview() {
    LostTheme { ArrivalContent("Biblioteca Central", {}, {}) }
}
