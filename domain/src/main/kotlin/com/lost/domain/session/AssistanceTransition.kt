package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState

/** Un cambio de estado de la asistencia. Quien lo observa decide vibrar, medir o aprender. */
data class AssistanceTransition(val from: AssistanceState, val to: AssistanceState, val event: AssistanceEvent) {
    val hasChanged: Boolean get() = from != to
}
