package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import java.io.InputStream
import java.time.Instant
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/** Lee los puntos (`trkpt`) de un archivo GPX con su latitud, longitud y hora. */
class GpxRouteReader @Inject constructor() {
    fun read(input: InputStream): List<GeoPoint> {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input)
        val nodes = document.getElementsByTagName(TRACK_POINT)
        return (0 until nodes.length).map { index -> toGeoPoint(nodes.item(index) as Element) }
    }

    private fun toGeoPoint(element: Element): GeoPoint {
        val time = element.getElementsByTagName(TIME).item(0)?.textContent
        return GeoPoint(
            latitude = element.getAttribute(LATITUDE).toDouble(),
            longitude = element.getAttribute(LONGITUDE).toDouble(),
            timestampMillis = time?.let { Instant.parse(it.trim()).toEpochMilli() } ?: 0L
        )
    }

    private companion object {
        const val TRACK_POINT = "trkpt"
        const val TIME = "time"
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
    }
}
