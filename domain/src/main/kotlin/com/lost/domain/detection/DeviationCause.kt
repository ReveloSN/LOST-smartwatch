package com.lost.domain.detection

/** La causa principal de una alerta, sin sus números, para guardarla y agrupar alertas por causa. */
enum class DeviationCause {
    LEFT_ROUTE,
    REPEATED_AREA,
    TAKING_LONGER,
    MOVING_AWAY,
    ERRATIC_DIRECTION,
    UNUSUAL_STOP;

    companion object {
        fun of(reason: DeviationReason): DeviationCause = when (reason) {
            is DeviationReason.LeftUsualRoute -> LEFT_ROUTE
            is DeviationReason.RepeatedArea -> REPEATED_AREA
            is DeviationReason.TakingLonger -> TAKING_LONGER
            DeviationReason.MovingAwayFromDestination -> MOVING_AWAY
            DeviationReason.ErraticDirection -> ERRATIC_DIRECTION
            DeviationReason.UnusualStop -> UNUSUAL_STOP
        }
    }
}
