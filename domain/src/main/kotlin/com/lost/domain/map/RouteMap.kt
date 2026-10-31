package com.lost.domain.map

/** Un mapa listo para dibujar: el encuadre y las teselas que se ven (las que fallaron se omiten). */
data class RouteMap(val viewport: MapViewport, val tiles: List<MapTile>)
