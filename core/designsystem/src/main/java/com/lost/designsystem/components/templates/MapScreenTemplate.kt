package com.lost.designsystem.components.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.lost.designsystem.components.atoms.LostChip
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.organisms.RouteComparisonContent
import com.lost.designsystem.components.organisms.RouteComparisonMap
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens.MapScreen
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Pantalla de mapa para reloj: el mapa ocupa el centro, el estado va arriba y una sola
 * idea abajo. Nada se desplaza: lo que no cabe pasa a un segundo nivel.
 */
@Composable
fun MapScreenTemplate(
    map: @Composable BoxScope.() -> Unit,
    top: @Composable ColumnScope.() -> Unit,
    bottom: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SemanticTokens.Background.base),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = map)
        EdgeColumn(Alignment.TopCenter, Modifier.padding(top = MapScreen.edgePadding), top)
        EdgeColumn(Alignment.BottomCenter, Modifier.padding(bottom = MapScreen.edgePadding), bottom)
    }
}

@Composable
private fun BoxScope.EdgeColumn(alignment: Alignment, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .align(alignment)
            .fillMaxWidth(MapScreen.TEXT_WIDTH_FRACTION),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs),
        content = content
    )
}

@LostDevicePreviews
@Composable
private fun MapScreenTemplatePreview() {
    LostPreviewSurface {
        MapScreenTemplate(
            map = {
                RouteComparisonMap(
                    RouteComparisonContent(
                        habitual = listOf(Offset(0.1f, 0.9f), Offset(0.5f, 0.5f), Offset(0.9f, 0.1f)),
                        today = listOf(Offset(0.1f, 0.9f), Offset(0.5f, 0.5f), Offset(0.6f, 0.7f)),
                        divergence = Offset(0.5f, 0.5f),
                        tone = StatusTone.DEVIATION,
                        description = PreviewSamples.MAP_DESCRIPTION
                    ),
                    Modifier.fillMaxHeight(MapScreen.MAP_HEIGHT_FRACTION).aspectRatio(1f)
                )
            },
            top = { LostChip(PreviewSamples.STATUS, icon = Icons.Rounded.CheckCircle, onClick = {}) },
            bottom = { LostText(PreviewSamples.REASON_DETAIL, role = LostTextRole.TITLE) }
        )
    }
}
