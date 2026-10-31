package com.lost.watch.feature.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ActionButtonContent
import com.lost.designsystem.components.molecules.DualActionButtons
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/**
 * 01.2 · Permiso de ubicación explicado en lenguaje humano, antes del diálogo del sistema.
 * "Ahora no" deja a LOST sin aprendizaje, pero se puede seguir.
 */
@Composable
fun LocationPermissionScreen(onContinue: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onContinue()
    }
    LocationPermissionContent(
        onAllow = {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        },
        onNotNow = onContinue
    )
}

@Composable
private fun LocationPermissionContent(onAllow: () -> Unit, onNotNow: () -> Unit) {
    WatchScreenTemplate {
        LostText(stringResource(R.string.onboarding_location_title), role = LostTextRole.QUESTION)
        LostText(
            stringResource(R.string.onboarding_location_body),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        DualActionButtons(
            primary = ActionButtonContent(stringResource(R.string.onboarding_allow), onAllow),
            secondary = ActionButtonContent(stringResource(R.string.onboarding_not_now), onNotNow)
        )
    }
}

@LostDevicePreviews
@Composable
private fun LocationPermissionPreview() {
    LostTheme { LocationPermissionContent(onAllow = {}, onNotNow = {}) }
}
