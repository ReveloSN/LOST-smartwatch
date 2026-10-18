package com.lost.designsystem.components.organisms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.lost.designsystem.tokens.ComponentTokens

/** Un arco del anillo perimetral: dónde empieza, cuánto recorre y de qué color es. */
internal data class RingArc(
    val color: Color,
    val startAngle: Float = ComponentTokens.StatusRing.START_ANGLE,
    val sweepAngle: Float = ComponentTokens.StatusRing.FULL_SWEEP
)

/**
 * Dibuja arcos que siguen el borde del reloj, a la distancia de la zona segura.
 * Lo comparten el anillo de estado, el arco de probabilidad y el anillo segmentado.
 */
internal class RingArcPainter(private val strokeWidthPx: Float, private val insetPx: Float) {
    fun DrawScope.drawArc(arc: RingArc) {
        val offset = insetPx + strokeWidthPx / 2
        val diameter = size.minDimension - offset * 2
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        drawArc(
            color = arc.color,
            startAngle = arc.startAngle,
            sweepAngle = arc.sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}
