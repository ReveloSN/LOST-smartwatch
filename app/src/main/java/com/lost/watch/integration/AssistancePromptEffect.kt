package com.lost.watch.integration

import com.lost.domain.session.TripSession
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Muestra o retira la pregunta por notificación (patrón Observer): aparece cuando LOST pregunta y la app no
 * está a la vista, y se retira al responder, al abrir la app o al terminar el recorrido.
 */
class AssistancePromptEffect @Inject constructor(
    private val session: TripSession,
    private val visibility: AppVisibility,
    private val policy: AssistancePromptPolicy,
    private val notifications: PromptNotifications
) {
    suspend fun run() {
        try {
            combine(session.snapshot, visibility.isVisible) { snapshot, isVisible ->
                policy.promptFor(snapshot.assistance, isVisible)
            }.distinctUntilChanged().collect { prompt ->
                if (prompt == null) notifications.cancel() else notifications.show(prompt)
            }
        } finally {
            notifications.cancel()
        }
    }
}
