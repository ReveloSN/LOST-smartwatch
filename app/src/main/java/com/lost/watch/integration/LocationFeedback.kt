package com.lost.watch.integration

import com.lost.domain.session.GuidanceTurnCue
import com.lost.domain.session.LocationSignalWatchdog
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Lo que LOST dice a partir de la ubicación mientras dura un recorrido: si se perdió y, guiando, cuándo girar. */
class LocationFeedback @Inject constructor(
    private val watchdog: LocationSignalWatchdog,
    private val turnCue: GuidanceTurnCue
) {
    suspend fun run(): Unit = coroutineScope {
        launch { watchdog.run() }
        launch { turnCue.run() }
    }
}
