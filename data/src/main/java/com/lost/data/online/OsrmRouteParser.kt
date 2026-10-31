package com.lost.data.online

import com.lost.domain.model.GeoPoint
import javax.inject.Inject
import org.json.JSONObject

/** Lee la geometría (GeoJSON, `[lon, lat]`) de la primera ruta que devuelve OSRM. */
class OsrmRouteParser @Inject constructor() {
    fun parse(json: String): List<GeoPoint> {
        val root = JSONObject(json)
        if (root.optString("code") != "Ok") return emptyList()
        val coordinates = root.getJSONArray("routes").getJSONObject(0)
            .getJSONObject("geometry").getJSONArray("coordinates")
        return (0 until coordinates.length()).map { index ->
            val pair = coordinates.getJSONArray(index)
            GeoPoint(latitude = pair.getDouble(1), longitude = pair.getDouble(0))
        }
    }
}
