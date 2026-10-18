package com.lost.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.LocationSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Patrón Adapter: convierte el Fused Location Provider de Google Play Services en el puerto
 * [LocationSource] del dominio. El dominio nunca sabe que existe Google.
 */
class FusedLocationSource @Inject constructor(@ApplicationContext private val context: Context) : LocationSource {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun locations(): Flow<GeoPoint> = if (hasPermission()) updates() else emptyFlow()

    @SuppressLint("MissingPermission")
    private fun updates(): Flow<GeoPoint> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { trySend(GeoPoint(it.latitude, it.longitude, it.time)) }
            }
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build()
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }

    private fun hasPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val INTERVAL_MILLIS = 5_000L
    }
}
