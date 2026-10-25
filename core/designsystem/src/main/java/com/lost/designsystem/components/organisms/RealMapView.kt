package com.lost.designsystem.components.organisms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens.RealMap
import com.lost.designsystem.tokens.ComponentTokens.RouteLine
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Mapa real para reloj: teselas de OpenStreetMap oscurecidas, la ruta de siempre en gris tenue,
 * la de hoy en el color del estado y el punto del usuario. No es interactivo (sin zoom ni arrastre)
 * para no chocar con el pager ni con deslizar-para-volver; el encuadre se ajusta solo.
 */
@Composable
fun RealMapView(content: RealMapContent, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clipToBounds()) {
        Canvas(modifier = Modifier.fillMaxSize().semantics { contentDescription = content.description }) {
            content.tiles.forEach { drawTile(it) }
            val casing = RealMap.casingExtraWidth.toPx()
            drawRoute(content.habitual, SemanticTokens.Route.casing, RealMap.habitualWidth.toPx() + casing)
            drawRoute(content.habitual, SemanticTokens.Route.habitualOnMap, RealMap.habitualWidth.toPx())
            drawRoute(content.today, SemanticTokens.Route.casing, RealMap.todayWidth.toPx() + casing)
            drawRoute(content.today, SemanticTokens.Route.today(content.tone), RealMap.todayWidth.toPx())
            content.today.lastOrNull()?.let { user ->
                drawCircle(SemanticTokens.Text.primary, RouteLine.userDotRadius.toPx(), at(user))
            }
        }
        content.attribution?.let { Attribution(it) }
    }
}

/** "© OpenStreetMap contributors": obligatoria por la licencia ODbL y siempre visible. */
@Composable
private fun BoxScope.Attribution(text: String) {
    LostText(
        text,
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = RealMap.attributionPadding),
        role = LostTextRole.CAPTION,
        color = SemanticTokens.Text.secondary
    )
}

private fun DrawScope.at(point: Offset) = Offset(point.x * size.width, point.y * size.height)

private fun DrawScope.drawTile(tile: MapTileImage) {
    drawImage(
        image = tile.image,
        dstOffset = IntOffset((tile.left * size.width).toInt(), (tile.top * size.height).toInt()),
        // +1 px para que no se vean costuras entre teselas al redondear.
        dstSize = IntSize((tile.width * size.width).toInt() + 1, (tile.height * size.height).toInt() + 1),
        colorFilter = DarkTileFilter.colorFilter
    )
}

private fun DrawScope.drawRoute(points: List<Offset>, color: Color, width: Float) {
    if (points.size < 2) return
    val path = Path().apply {
        at(points.first()).let { moveTo(it.x, it.y) }
        points.drop(1).forEach { at(it).let { p -> lineTo(p.x, p.y) } }
    }
    drawPath(path, color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@LostDevicePreviews
@Composable
private fun RealMapViewPreview() {
    LostPreviewSurface {
        RealMapView(
            RealMapContent(
                tiles = listOf(MapTileImage(ImageBitmap(1, 1), 0f, 0f, 1f, 1f)),
                habitual = listOf(Offset(0.2f, 0.8f), Offset(0.5f, 0.5f), Offset(0.8f, 0.2f)),
                today = listOf(Offset(0.2f, 0.8f), Offset(0.5f, 0.5f), Offset(0.6f, 0.7f)),
                tone = StatusTone.DEVIATION,
                description = PreviewSamples.MAP_DESCRIPTION,
                attribution = PreviewSamples.MAP_ATTRIBUTION
            ),
            Modifier.fillMaxSize()
        )
    }
}
