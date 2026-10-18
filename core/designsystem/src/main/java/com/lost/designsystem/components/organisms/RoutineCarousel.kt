package com.lost.designsystem.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.RoutineCard
import com.lost.designsystem.components.molecules.RoutineCardContent
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Lista circular: el elemento enfocado crece al centro y los demás se encogen y atenúan
 * hacia el borde. Se desplaza con la corona rotatoria.
 */
@Composable
fun RoutineCarousel(
    title: String,
    items: List<RoutineCarouselItem>,
    modifier: Modifier = Modifier,
    state: ScalingLazyListState = rememberScalingLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    ScalingLazyColumn(modifier = modifier, state = state, contentPadding = contentPadding) {
        item(key = "header") {
            LostText(text = title, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        }
        items(items, key = { it.key }) { item ->
            RoutineCard(content = item.content, onClick = item.onClick)
        }
    }
}

@LostDevicePreviews
@Composable
private fun RoutineCarouselPreview() {
    LostPreviewSurface {
        RoutineCarousel(
            title = PreviewSamples.ROUTINE_TITLE,
            items = listOf(
                RoutineCarouselItem(
                    "gym",
                    RoutineCardContent(
                        Icons.Rounded.FitnessCenter,
                        PreviewSamples.ROUTINE_TITLE,
                        PreviewSamples.ROUTINE_DETAIL
                    )
                ) {
                },
                RoutineCarouselItem(
                    "u",
                    RoutineCardContent(
                        Icons.Rounded.School,
                        PreviewSamples.ROUTINE_TITLE,
                        PreviewSamples.ROUTINE_DETAIL
                    )
                ) {
                },
                RoutineCarouselItem(
                    "home",
                    RoutineCardContent(
                        Icons.Rounded.Home,
                        PreviewSamples.LONG_ROUTINE_TITLE,
                        PreviewSamples.ROUTINE_DETAIL
                    )
                ) {
                }
            )
        )
    }
}
