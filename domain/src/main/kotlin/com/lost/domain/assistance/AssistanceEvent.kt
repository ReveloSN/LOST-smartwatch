package com.lost.domain.assistance

import com.lost.domain.detection.DetectionResult

/** Lo que puede pasar mientras LOST acompaña un recorrido. */
sealed interface AssistanceEvent {
    data class DetectionUpdated(val result: DetectionResult, val hasAuthorizedContact: Boolean) : AssistanceEvent

    data object UserRequestedGuidance : AssistanceEvent

    data object UserIsFine : AssistanceEvent

    data object UserIsGoingElsewhere : AssistanceEvent

    /** "Ya sé dónde estoy": el usuario deja la guía sin haber vuelto a la ruta. */
    data object UserEndedGuidance : AssistanceEvent

    /** Pasaron 30 s sin respuesta a la pregunta. */
    data class ResponseTimedOut(val hasAuthorizedContact: Boolean) : AssistanceEvent

    data object CountdownTicked : AssistanceEvent

    data object CountdownCancelled : AssistanceEvent

    data object RouteRecovered : AssistanceEvent

    data object DestinationReached : AssistanceEvent

    data object AcknowledgementElapsed : AssistanceEvent
}
