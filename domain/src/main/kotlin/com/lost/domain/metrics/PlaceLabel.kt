package com.lost.domain.metrics

/** Cómo se nombra un lugar sin enviar su ubicación a ningún servidor. La UI lo traduce. */
sealed interface PlaceLabel {
    /** A menos de 250 m de un destino guardado: "Cerca de Biblioteca Central". */
    data class NearDestination(val name: String) : PlaceLabel

    /** Durante una rutina: "En tu ruta Casa → U". */
    data class OnRoutine(val name: String) : PlaceLabel

    data object Unknown : PlaceLabel
}
