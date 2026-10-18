package com.lost.watch.feature.monitoring

import androidx.annotation.StringRes
import com.lost.watch.R

/** Titular del monitoreo según qué tanto se parece hoy a la ruta de siempre. */
enum class MonitoringHeadline(@param:StringRes val text: Int) {
    IDLE(R.string.monitoring_idle),
    ON_ROUTE(R.string.monitoring_on_route),
    OTHER_STREET(R.string.monitoring_other_street),
    DRIFTING(R.string.monitoring_drifting),
    NOT_SIMILAR(R.string.monitoring_not_similar),
    NEW_PLACE(R.string.monitoring_new_place)
}
