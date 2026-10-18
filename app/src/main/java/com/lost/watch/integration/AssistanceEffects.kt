package com.lost.watch.integration

import com.lost.domain.assistance.AssistanceFeedbackLearner
import com.lost.domain.assistance.AssistanceHapticPolicy
import com.lost.domain.metrics.AssistanceMetricsRecorder
import com.lost.domain.port.HapticNotifier
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.session.TripSession
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Efectos de cada transición de asistencia (patrón Observer): vibrar con el patrón del estado,
 * medir, aprender de la respuesta y avisar al contacto. La máquina de estados sigue siendo pura.
 */
class AssistanceEffects @Inject constructor(
    private val session: TripSession,
    private val hapticPolicy: AssistanceHapticPolicy,
    private val hapticNotifier: HapticNotifier,
    private val reactions: AssistanceReactions
) {
    suspend fun run(): Unit = coroutineScope {
        session.transitions.collect { transition ->
            hapticPolicy.patternFor(transition)?.let(hapticNotifier::play)
            launch { reactions.onTransition(transition) }
        }
    }
}
