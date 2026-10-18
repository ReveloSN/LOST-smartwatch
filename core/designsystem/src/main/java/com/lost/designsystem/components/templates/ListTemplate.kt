package com.lost.designsystem.components.templates

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.RoutineCard
import com.lost.designsystem.components.molecules.RoutineCardContent
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples

/**
 * Plantilla de lista circular con título. Incluye el indicador de desplazamiento curvo
 * del borde derecho y el desplazamiento con la corona (lo da [ScreenScaffold]).
 */
@Composable
fun ListTemplate(
    title: String,
    modifier: Modifier = Modifier,
    state: ScalingLazyListState = rememberScalingLazyListState(),
    content: ScalingLazyListScope.() -> Unit
) {
    ScreenScaffold(scrollState = state, modifier = modifier) { contentPadding ->
        ScalingLazyColumn(state = state, contentPadding = contentPadding) {
            item(key = "list-title") {
                ListHeader(modifier = Modifier.fillMaxWidth()) {
                    LostText(text = title, role = LostTextRole.TITLE)
                }
            }
            content()
        }
    }
}

@LostDevicePreviews
@Composable
private fun ListTemplatePreview() {
    LostPreviewSurface {
        ListTemplate(title = PreviewSamples.ROUTINE_TITLE) {
            items(count = 3) {
                RoutineCard(
                    RoutineCardContent(Icons.Rounded.Tune, PreviewSamples.ROUTINE_TITLE, PreviewSamples.ROUTINE_DETAIL),
                    onClick = {}
                )
            }
        }
    }
}
