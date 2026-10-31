package com.lost.domain.session

/** Distancias y tiempos con los que el monitor decide qué pasó y cada cuánto mirar. */
object MonitoringThresholds {
    /** A menos de esto del punto más cercano de la ruta de siempre = "Ya estás en tu ruta". */
    const val ROUTE_RECOVERED_METERS = 25.0

    /** Tras recuperar la ruta, lo anterior se perdona mientras siga a menos de esto de ella. */
    const val GRACE_RADIUS_METERS = 40.0

    /** A menos de esto del destino = "Llegaste". */
    const val ARRIVAL_METERS = 30.0

    /** Mientras todo está en calma el motor evalúa como mucho una vez cada 4 s de GPS. */
    const val MIN_EVALUATION_INTERVAL_MILLIS = 4_000L

    /** Tope de puntos del recorrido en memoria (2 h con un punto por segundo). */
    const val MAX_TRIP_POINTS = 7_200

    /** GPS adaptativo: en rutina y en calma, cada 15 s; si algo no cuadra, cada 5 s; guiando, cada 2 s. */
    const val GPS_RELAXED_MILLIS = 15_000L
    const val GPS_ATTENTIVE_MILLIS = 5_000L
    const val GPS_GUIDING_MILLIS = 2_000L
}
