package com.lost.domain.metrics

/** Distancias y tiempos de los resúmenes para el usuario (03.2 · Lugares donde más te desorientas). */
object InsightsThresholds {
    /** Dos alertas a menos de esto (una cuadra) son el mismo lugar. */
    const val ALERT_PLACE_RADIUS_METERS = 100.0

    /** A menos de esto de un destino guardado, el lugar se llama "Cerca de {destino}". */
    const val NEAR_DESTINATION_METERS = 250.0

    /** Días hacia atrás que se miran. */
    const val PLACES_WINDOW_DAYS = 30L

    /** Lugares que se muestran como mucho (la pantalla del reloj es pequeña). */
    const val MAX_PLACES = 3
}
