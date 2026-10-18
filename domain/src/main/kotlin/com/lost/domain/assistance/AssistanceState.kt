package com.lost.domain.assistance

import com.lost.domain.detection.DeviationReason

/**
 * Estados de la escalada de ayuda (patrón State). Cada estado es inmutable;
 * [AssistanceStateMachine] decide el siguiente a partir de un [AssistanceEvent].
 */
sealed interface AssistanceState {
    /** Todo en orden: el anillo respira y LOST solo observa. */
    data object Calm : AssistanceState

    /** Desvío voluntario: "¿Vas a otro lado hoy?". Si no responde, no insiste. */
    data class SoftAsk(val reasons: List<DeviationReason>) : AssistanceState

    /** Nivel medio: "Te saliste de tu ruta de siempre. ¿Necesitas ayuda?". */
    data class AskForHelp(val reasons: List<DeviationReason>) : AssistanceState

    /** Guía de vuelta a la ruta de siempre (flecha + distancia). */
    data object Guiding : AssistanceState

    /** Aviso al contacto en N segundos; se puede cancelar. */
    data class SharingCountdown(val secondsLeft: Int) : AssistanceState

    /** El contacto ya sabe dónde está el usuario. */
    data object LocationShared : AssistanceState

    /** "Entendido. LOST aprenderá de esto." Vuelve a Calm tras unos segundos. */
    data class Acknowledged(val feedback: UserFeedback) : AssistanceState
}
