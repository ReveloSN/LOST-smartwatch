package com.lost.data.demo

import android.content.Context
import androidx.annotation.StringRes
import com.lost.data.R
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import kotlin.math.cos

/**
 * Destinos de ejemplo de la lámina 02. No tienen coordenadas fijas: se ubican alrededor de la
 * llegada de la ruta de siempre (el campus) y de la llegada del recorrido de lugar nuevo (la clínica),
 * así que al regenerar los GPX con otro centro (`tools/generate_osm_routes.py`) se mueven solos.
 */
internal object DemoDestinations {
    private data class DemoPlace(
        val id: String,
        @StringRes val name: Int,
        val kind: PlaceKind,
        val offset: MetersOffset,
        val isNearClinic: Boolean = false
    )

    private data class MetersOffset(val east: Double, val north: Double)

    private val places = listOf(
        DemoPlace(
            "demo-lab",
            R.string.demo_destination_lab,
            PlaceKind.UNIVERSITY,
            MetersOffset(east = -120.0, north = 90.0)
        ),
        DemoPlace(
            "demo-library",
            R.string.demo_destination_library,
            PlaceKind.LIBRARY,
            MetersOffset(east = -60.0, north = 160.0)
        ),
        DemoPlace(
            "demo-cafe",
            R.string.demo_destination_cafe,
            PlaceKind.RESTAURANT,
            MetersOffset(east = 40.0, north = 60.0)
        ),
        DemoPlace(
            "demo-hospital",
            R.string.demo_destination_hospital,
            PlaceKind.HOSPITAL,
            MetersOffset(east = 0.0, north = 0.0),
            isNearClinic = true
        )
    )

    fun all(context: Context, campus: GeoPoint, clinic: GeoPoint): List<Destination> = places.map { place ->
        val anchor = if (place.isNearClinic) clinic else campus
        Destination(place.id, context.getString(place.name), anchor.moved(place.offset), place.kind, null)
    }

    private fun GeoPoint.moved(offset: MetersOffset): GeoPoint = GeoPoint(
        latitude = latitude + offset.north / METERS_PER_DEGREE,
        longitude = longitude + offset.east / (METERS_PER_DEGREE * cos(Math.toRadians(latitude)))
    )

    private const val METERS_PER_DEGREE = 111_195.0
}
