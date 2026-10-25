package com.lost.data.online

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceSuggestion
import javax.inject.Inject
import org.json.JSONArray

/** Convierte la respuesta de Nominatim (`format=jsonv2`) en lugares con nombre corto y zona. */
class NominatimResponseParser @Inject constructor() {
    fun parse(json: String): List<PlaceSuggestion> {
        val results = JSONArray(json)
        return (0 until results.length()).map { index ->
            val place = results.getJSONObject(index)
            val parts = place.optString("display_name").split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val name = place.optString("name").ifBlank { parts.firstOrNull().orEmpty() }
            PlaceSuggestion(
                name = name,
                detail = parts.filter { it != name }.take(DETAIL_PARTS).joinToString(", "),
                location = GeoPoint(place.getString("lat").toDouble(), place.getString("lon").toDouble())
            )
        }
    }

    private companion object {
        const val DETAIL_PARTS = 2
    }
}
