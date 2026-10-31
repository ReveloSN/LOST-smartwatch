package com.lost.data.online

/**
 * Reglas de uso de los servicios de OpenStreetMap: identificarse con un User-Agent propio,
 * esperar poco y no reintentar en bucle. Las teselas además se guardan en caché.
 */
internal object OsmUsagePolicy {
    const val USER_AGENT = "LOST-smartwatch/1.0 (Wear OS student project; github.com/ReveloSN/LOST-smartwatch)"
    const val CONNECT_TIMEOUT_MILLIS = 8_000
    const val READ_TIMEOUT_MILLIS = 10_000
    const val TILE_URL = "https://tile.openstreetmap.org/%d/%d/%d.png"
    const val SEARCH_URL = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=%d&accept-language=%s&q=%s"
    const val WALKING_ROUTE_URL =
        "https://routing.openstreetmap.de/routed-foot/route/v1/foot/%s,%s;%s,%s?overview=full&geometries=geojson"
}
