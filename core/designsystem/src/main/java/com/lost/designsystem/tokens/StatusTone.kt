package com.lost.designsystem.tokens

/**
 * Los tres estados visuales de LOST. El design system no conoce el dominio:
 * la app traduce su nivel de asistencia a uno de estos tonos.
 */
enum class StatusTone {
    ON_ROUTE,
    DEVIATION,
    HELP
}
