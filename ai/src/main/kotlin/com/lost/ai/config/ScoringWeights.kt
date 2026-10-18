package com.lost.ai.config

/**
 * Pesos de la regresión logística:
 * z = bias + w1·desvío + w2·confusión + w3·retraso + w4·(desvío·confusión), probabilidad = sigmoid(z).
 * El término de interacción separa el desvío voluntario (desvío alto, confusión baja) de la desorientación.
 */
data class ScoringWeights(
    val bias: Double,
    val deviation: Double,
    val confusion: Double,
    val delay: Double,
    val interaction: Double
) {
    companion object {
        val ROUTINE = ScoringWeights(bias = -4.0, deviation = 1.5, confusion = 2.0, delay = 1.0, interaction = 8.0)

        /** Lugar nuevo: no hay ruta de referencia, solo cuentan las señales de confusión. */
        val NEW_PLACE = ScoringWeights(bias = -3.0, deviation = 0.0, confusion = 5.0, delay = 0.0, interaction = 0.0)
    }
}
