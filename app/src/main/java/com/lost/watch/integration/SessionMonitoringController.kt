package com.lost.watch.integration

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.lost.data.location.LocationTrackingService
import com.lost.domain.model.Destination
import com.lost.domain.session.TripSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Empieza y termina el acompañamiento: abre la sesión, arranca la tubería y, si hay permiso
 * de ubicación, un servicio en primer plano que mantiene vivo el GPS con la pantalla apagada.
 */
class SessionMonitoringController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val session: TripSession,
    private val runner: TripMonitorRunner
) : MonitoringController {
    override fun start(destination: Destination?) {
        session.start(destination)
        runner.start()
        if (hasLocationPermission()) LocationTrackingService.start(context)
    }

    override fun stop() {
        LocationTrackingService.stop(context)
        runner.stop(onFinished = session::stop)
    }

    private fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
