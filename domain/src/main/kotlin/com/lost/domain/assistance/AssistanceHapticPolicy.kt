package com.lost.domain.assistance

import com.lost.domain.session.AssistanceTransition
import javax.inject.Inject

/**
 * Qué vibración acompaña cada transición (la háptica comunica el estado):
 * pregunta suave → suave · ¿necesitas ayuda? → doble · cuenta regresiva → insistente · guía → giro.
 */
class AssistanceHapticPolicy @Inject constructor() {
    fun patternFor(transition: AssistanceTransition): HapticPattern? {
        if (!transition.hasChanged) return null
        val from = transition.from
        return when (transition.to) {
            is AssistanceState.SoftAsk -> HapticPattern.GENTLE

            is AssistanceState.AskForHelp -> HapticPattern.DOUBLE.takeIf { from !is AssistanceState.AskForHelp }

            is AssistanceState.SharingCountdown -> HapticPattern.INSISTENT.takeIf {
                from !is AssistanceState.SharingCountdown
            }

            AssistanceState.Guiding -> HapticPattern.DIRECTION

            AssistanceState.LocationShared, is AssistanceState.Acknowledged -> HapticPattern.GENTLE

            AssistanceState.Calm -> HapticPattern.GENTLE.takeIf { from == AssistanceState.Guiding }
        }
    }
}
