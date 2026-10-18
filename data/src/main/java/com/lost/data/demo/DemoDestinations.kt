package com.lost.data.demo

import android.content.Context
import androidx.annotation.StringRes
import com.lost.data.R
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind

/** Destinos de ejemplo de la lámina 02 (campus y hospital). Los nombres vienen de strings.xml. */
internal object DemoDestinations {
    private data class DemoPlace(val id: String, @StringRes val name: Int, val location: GeoPoint, val kind: PlaceKind)

    private val places = listOf(
        DemoPlace(
            "demo-lab",
            R.string.demo_destination_lab,
            GeoPoint(latitude = 4.6131, longitude = -74.0795),
            PlaceKind.UNIVERSITY
        ),
        DemoPlace(
            "demo-library",
            R.string.demo_destination_library,
            GeoPoint(latitude = 4.6125, longitude = -74.0790),
            PlaceKind.LIBRARY
        ),
        DemoPlace(
            "demo-cafe",
            R.string.demo_destination_cafe,
            GeoPoint(latitude = 4.6120, longitude = -74.0801),
            PlaceKind.RESTAURANT
        ),
        DemoPlace(
            "demo-hospital",
            R.string.demo_destination_hospital,
            GeoPoint(latitude = 4.6307, longitude = -74.0640),
            PlaceKind.HOSPITAL
        )
    )

    fun all(context: Context): List<Destination> = places.map { place ->
        Destination(place.id, context.getString(place.name), place.location, place.kind, lastUsedAtMillis = null)
    }
}
