package com.lost.domain.metrics

import com.lost.domain.detection.DeviationCause
import com.lost.domain.model.GeoPoint

/** 03.2 · Un lugar donde LOST preguntó varias veces: cuántas, la causa más frecuente y cómo llamarlo. */
data class DisorientationPlace(
    val center: GeoPoint,
    val times: Int,
    val mainCause: DeviationCause?,
    val label: PlaceLabel
)
