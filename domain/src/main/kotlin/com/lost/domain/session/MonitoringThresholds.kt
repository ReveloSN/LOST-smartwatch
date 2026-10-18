package com.lost.domain.session

/** Distancias con las que el monitor decide que el usuario volvió a su ruta o llegó. */
object MonitoringThresholds {
    /** A menos de esto del punto más cercano de la ruta de siempre = "Ya estás en tu ruta". */
    const val ROUTE_RECOVERED_METERS = 25.0

    /** Tras recuperar la ruta, lo anterior se perdona mientras siga a menos de esto de ella. */
    const val GRACE_RADIUS_METERS = 40.0

    /** A menos de esto del destino = "Llegaste". */
    const val ARRIVAL_METERS = 30.0
}
