package com.lost.watch.integration

import com.lost.domain.model.Destination
import com.lost.domain.session.TripSession
import javax.inject.Inject

/** Empieza y termina la sesión de recorrido. */
class SessionMonitoringController @Inject constructor(private val session: TripSession) : MonitoringController {
    override fun start(destination: Destination?) = session.start(destination)

    override fun stop() = session.stop()
}
