package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import javax.inject.Inject

/** Lee los puntos del GPX de un escenario de la demo. */
class DemoGpxLoader @Inject constructor(private val opener: GpxAssetOpener, private val reader: GpxRouteReader) {
    fun points(scenario: DemoScenario): List<GeoPoint> = opener.open(scenario.assetName).use(reader::read)
}
