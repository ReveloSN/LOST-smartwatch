package com.lost.domain.metrics

/**
 * Un evento de uso. [elapsedMillis] guarda una duración cuando aplica
 * (por ejemplo, de la alerta a la respuesta del usuario).
 */
data class MetricEvent(val type: MetricEventType, val timestampMillis: Long, val elapsedMillis: Long? = null)
