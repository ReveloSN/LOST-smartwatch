package com.lost.data.location

import com.lost.data.BuildConfig
import com.lost.data.demo.SimulatedLocationSource
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.LocationSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Fuente de ubicación que usa la app: el GPS real o, en modo demo, los GPX simulados. */
class ActiveLocationSource @Inject constructor(
    private val fused: FusedLocationSource,
    private val simulated: SimulatedLocationSource
) : LocationSource {
    override fun locations(): Flow<GeoPoint> = if (BuildConfig.DEMO_MODE) simulated.locations() else fused.locations()
}
