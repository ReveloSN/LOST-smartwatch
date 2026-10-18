package com.lost.watch.feature.assistance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.molecules.ActionButtonContent
import com.lost.designsystem.components.molecules.DualActionButtons
import com.lost.designsystem.components.templates.AlertHeader
import com.lost.designsystem.components.templates.AlertTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R

private const val SOFT_ARC = 0.25f

/** V.2 · Pregunta suave ante un desvío voluntario. Una sola vibración; si no responde, LOST no insiste. */
@Composable
fun SoftAskAlert(onEvent: (AssistanceUiEvent) -> Unit) {
    AlertTemplate(
        header = AlertHeader(
            Icons.Rounded.Storefront,
            null,
            stringResource(R.string.assist_soft_ask_title),
            StatusTone.ON_ROUTE
        ),
        arcFraction = SOFT_ARC
    ) {
        DualActionButtons(
            primary = ActionButtonContent(stringResource(R.string.assist_going_elsewhere)) {
                onEvent(AssistanceUiEvent.GoingElsewhere)
            },
            secondary = ActionButtonContent(stringResource(R.string.assist_guide_me)) {
                onEvent(AssistanceUiEvent.GuideMe)
            }
        )
    }
}

@LostDevicePreviews
@Composable
private fun SoftAskPreview() {
    LostTheme { SoftAskAlert {} }
}
