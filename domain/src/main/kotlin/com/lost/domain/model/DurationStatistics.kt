package com.lost.domain.model

/**
 * Media y desviación de la duración de una rutina, en minutos.
 * Se guarda la suma de cuadrados (`m2`) para poder seguir actualizando con Welford
 * sin almacenar todos los recorridos.
 */
data class DurationStatistics(val count: Int, val meanMinutes: Double, val m2: Double) {
    val varianceMinutes: Double get() = if (count < 2) 0.0 else m2 / (count - 1)
    val standardDeviationMinutes: Double get() = kotlin.math.sqrt(varianceMinutes)

    companion object {
        val EMPTY = DurationStatistics(count = 0, meanMinutes = 0.0, m2 = 0.0)
    }
}
