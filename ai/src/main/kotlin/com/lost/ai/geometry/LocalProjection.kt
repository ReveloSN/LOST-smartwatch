package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import kotlin.math.cos

/**
 * Proyección plana local (equirectangular) alrededor de un origen. A escala de un barrio
 * el error es despreciable y permite trabajar en metros con geometría simple.
 */
class LocalProjection(private val origin: GeoPoint) {
    private val metersPerDegree = Math.toRadians(1.0) * HaversineDistanceCalculator.EARTH_RADIUS_METERS
    private val longitudeScale = cos(Math.toRadians(origin.latitude))

    fun toMeters(point: GeoPoint): PlanePoint = PlanePoint(
        x = (point.longitude - origin.longitude) * metersPerDegree * longitudeScale,
        y = (point.latitude - origin.latitude) * metersPerDegree
    )

    fun toGeoPoint(plane: PlanePoint, timestampMillis: Long = 0L): GeoPoint = GeoPoint(
        latitude = origin.latitude + plane.y / metersPerDegree,
        longitude = origin.longitude + plane.x / (metersPerDegree * longitudeScale),
        timestampMillis = timestampMillis
    )
}
