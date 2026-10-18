package com.lost.ai.explanation

import com.lost.ai.signals.SignalReading
import com.lost.domain.detection.DeviationReason

/**
 * IA explicable: arma la lista de razones (sin texto) a partir de las señales que se activaron,
 * de la más fuerte a la más débil. La UI las convierte en frases con strings.xml.
 */
class DeviationExplanationBuilder(private val minScore: Double) {
    fun build(readings: List<SignalReading>): List<DeviationReason> = readings
        .filter { it.isApplicable && it.score >= minScore && it.reason != null }
        .sortedByDescending { it.score }
        .mapNotNull { it.reason }
        .distinctBy { it::class }
}
