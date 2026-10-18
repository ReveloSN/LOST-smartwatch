package com.lost.watch.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.PageIndicator
import com.lost.designsystem.components.organisms.BreathingStatusRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R

private const val WELCOME_RING_PROGRESS = 0.84f

/** 01.1 · "Hola, soy LOST": una idea por pantalla. */
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    WatchScreenTemplate(ring = { BreathingStatusRing(StatusTone.ON_ROUTE, WELCOME_RING_PROGRESS) }) {
        LostText(stringResource(R.string.onboarding_welcome_title), role = LostTextRole.QUESTION)
        LostText(stringResource(R.string.onboarding_welcome_body), color = SemanticTokens.Text.secondary)
        PageIndicator(pageCount = 3, selectedPage = 0, description = stringResource(R.string.onboarding_welcome_title))
        LostPrimaryButton(label = stringResource(R.string.onboarding_start), onClick = onStart)
    }
}

@LostDevicePreviews
@Composable
private fun WelcomeScreenPreview() {
    LostTheme { WelcomeScreen(onStart = {}) }
}
