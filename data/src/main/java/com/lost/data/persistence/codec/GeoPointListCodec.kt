package com.lost.data.persistence.codec

import com.lost.domain.model.GeoPoint

/**
 * Guarda una lista de puntos como texto compacto "lat,lon,tiempo;lat,lon,tiempo".
 * Evita una tabla de puntos por recorrido y no necesita una librería de JSON.
 */
object GeoPointListCodec {
    private const val POINT_SEPARATOR = ";"
    private const val FIELD_SEPARATOR = ","
    private const val FIELDS_PER_POINT = 3

    fun encode(points: List<GeoPoint>): String = points.joinToString(POINT_SEPARATOR) {
        "${it.latitude}$FIELD_SEPARATOR${it.longitude}$FIELD_SEPARATOR${it.timestampMillis}"
    }

    fun decode(text: String): List<GeoPoint> = text
        .split(POINT_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull(::decodePoint)

    private fun decodePoint(raw: String): GeoPoint? {
        val fields = raw.split(FIELD_SEPARATOR).takeIf { it.size == FIELDS_PER_POINT } ?: return null
        val latitude = fields[0].toDoubleOrNull()
        val longitude = fields[1].toDoubleOrNull()
        val timestamp = fields[2].toLongOrNull()
        return if (latitude == null || longitude == null || timestamp == null) {
            null
        } else {
            GeoPoint(latitude, longitude, timestamp)
        }
    }
}
