package com.lost.data.demo

/** Recorridos GPX de `sample-routes/` que se pueden reproducir en el modo demo. */
enum class DemoScenario(val assetName: String) {
    NORMAL_ROUTINE("demo_normal.gpx"),
    VOLUNTARY_DETOUR("demo_voluntary_detour.gpx"),
    DISORIENTATION("demo_disorientation.gpx"),
    NEW_PLACE("demo_new_place.gpx")
}
