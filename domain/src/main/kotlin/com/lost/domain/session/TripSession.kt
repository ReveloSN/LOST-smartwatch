package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.AssistanceStateMachine
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.model.Destination
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado compartido del recorrido en curso (patrón Observer con `StateFlow`).
 * La UI lo observa y le envía las respuestas del usuario; el monitor de recorrido lo alimenta
 * con cada nueva evaluación. Las transiciones de asistencia se publican para los efectos.
 */
@Singleton
class TripSession @Inject constructor() {
    private val stateMachine = AssistanceStateMachine()
    private val state = MutableStateFlow(TripSessionSnapshot.IDLE)
    private val transitionEvents = MutableSharedFlow<AssistanceTransition>(
        extraBufferCapacity = TRANSITION_BUFFER,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val snapshot: StateFlow<TripSessionSnapshot> = state.asStateFlow()
    val transitions: SharedFlow<AssistanceTransition> = transitionEvents.asSharedFlow()

    fun start(destination: Destination? = null) {
        state.value = TripSessionSnapshot.IDLE.copy(isMonitoring = true, destination = destination)
    }

    fun stop() {
        state.value = TripSessionSnapshot.IDLE
    }

    /** Lo usa el monitor para publicar el recorrido actualizado y su evaluación. */
    fun publish(transform: (TripSessionSnapshot) -> TripSessionSnapshot) = state.update(transform)

    /**
     * Aplica un evento a la máquina de estados y publica la transición. Si el usuario dice "Voy a otro lado",
     * la sesión lo recuerda hasta el final del recorrido. Es sincronizado: el monitor, los temporizadores, la UI
     * y la notificación despachan desde hilos distintos, y leer el estado y escribir el siguiente debe ser
     * una sola operación para que ninguna respuesta se pierda.
     */
    @Synchronized
    fun dispatch(event: AssistanceEvent): AssistanceTransition {
        val current = state.value.assistance
        val next = stateMachine.next(current, event)
        val isGoingElsewhere = next == AssistanceState.Acknowledged(UserFeedback.GOING_ELSEWHERE)
        state.update { it.copy(assistance = next, isGoingElsewhere = it.isGoingElsewhere || isGoingElsewhere) }
        val transition = AssistanceTransition(current, next, event)
        transitionEvents.tryEmit(transition)
        return transition
    }

    private companion object {
        const val TRANSITION_BUFFER = 16
    }
}
