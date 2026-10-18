package com.lost.designsystem.components.organisms

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens.RouteLine
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/** Ruta de siempre en gris al 28 %, hoy en el color del estado y un pulso donde se separan. */
@Composable
fun RouteComparisonMap(content: RouteComparisonContent, modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "divergencePulse").animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(SemanticTokens.Motion.BREATHE_MILLIS / 2), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val todayColor = SemanticTokens.Route.today(content.tone)
    Canvas(modifier = modifier.semantics { contentDescription = content.description }) {
        drawPolyline(content.habitual, SemanticTokens.Route.habitual, RouteLine.habitualWidth.toPx())
        drawPolyline(content.today, todayColor, RouteLine.todayWidth.toPx())
        content.divergence?.let { point ->
            drawCircle(
                todayColor.copy(alpha = 1f / pulse),
                RouteLine.divergencePulseRadius.toPx() * pulse,
                scaled(point)
            )
        }
        content.today.lastOrNull()?.let { userPoint ->
            drawCircle(SemanticTokens.Text.primary, RouteLine.userDotRadius.toPx(), scaled(userPoint))
        }
    }
}

private fun DrawScope.scaled(point: Offset) = Offset(point.x * size.width, point.y * size.height)

private fun DrawScope.drawPolyline(points: List<Offset>, color: Color, width: Float) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(scaled(points.first()).x, scaled(points.first()).y)
        points.drop(1).forEach { scaled(it).let { p -> lineTo(p.x, p.y) } }
    }
    drawPath(path, color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@LostDevicePreviews
@Composable
private fun RouteComparisonMapPreview() {
    LostPreviewSurface {
        RouteComparisonMap(
            RouteComparisonContent(
                habitual = listOf(Offset(0.1f, 0.9f), Offset(0.4f, 0.6f), Offset(0.6f, 0.4f), Offset(0.9f, 0.1f)),
                today = listOf(Offset(0.1f, 0.9f), Offset(0.4f, 0.6f), Offset(0.5f, 0.7f), Offset(0.6f, 0.6f)),
                divergence = Offset(0.4f, 0.6f),
                tone = StatusTone.DEVIATION,
                description = PreviewSamples.MAP_DESCRIPTION
            )
        )
    }
}
