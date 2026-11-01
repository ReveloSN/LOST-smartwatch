package com.lost.watch.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Support
import androidx.compose.ui.graphics.vector.ImageVector
import com.lost.designsystem.tokens.StatusTone
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.R

/** El estado en sus canales visuales: color (tono), icono y texto. La vibración la pone la integración. */
enum class StatusPresentation(val tone: StatusTone, val icon: ImageVector, @param:StringRes val label: Int) {
    ON_ROUTE(StatusTone.ON_ROUTE, Icons.Rounded.CheckCircle, R.string.status_on_route),
    DEVIATION(StatusTone.DEVIATION, Icons.AutoMirrored.Rounded.Help, R.string.status_deviation),
    HELP(StatusTone.HELP, Icons.Rounded.Support, R.string.status_help);

    companion object {
        fun of(level: AssistanceLevel): StatusPresentation = when (level) {
            AssistanceLevel.LOW -> ON_ROUTE
            AssistanceLevel.MEDIUM -> DEVIATION
            AssistanceLevel.HIGH -> HELP
        }

        /**
         * El estado de un recorrido en curso. Tras "Estoy bien" LOST no pregunta en 2 minutos; mostrar "Creo que
         * necesitas ayuda" en ese rato contradiría la respuesta, así que se queda como mucho en "Posible
         * desorientación": honesto, sin alarmar. Inicio, Monitoreo, la complicación y la vista rápida lo usan igual.
         */
        fun of(snapshot: TripSessionSnapshot): StatusPresentation {
            val status = of(snapshot.detection.level)
            val isPaused = snapshot.assistance == AssistanceState.Paused
            return if (isPaused && status == HELP) DEVIATION else status
        }
    }
}
