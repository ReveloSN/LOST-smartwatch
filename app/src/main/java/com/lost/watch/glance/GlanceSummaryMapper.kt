package com.lost.watch.glance

import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.R
import com.lost.watch.feature.home.HomeUiState
import com.lost.watch.feature.home.HomeUiStateMapper
import com.lost.watch.ui.StatusPresentation
import java.time.ZoneId
import javax.inject.Inject

/**
 * Resume el recorrido para la complicación y la vista rápida a partir del mismo estado que Inicio, así las
 * tres superficies dicen siempre lo mismo (lógica pura, probada sin Android).
 */
class GlanceSummaryMapper @Inject constructor(private val homeMapper: HomeUiStateMapper) {
    fun map(snapshot: TripSessionSnapshot, zoneId: ZoneId = ZoneId.systemDefault()): GlanceSummary {
        val home = homeMapper.map(snapshot, isDemoAvailable = false, zoneId = zoneId)
        return GlanceSummary(
            isMonitoring = home.isMonitoring,
            title = titleOf(home),
            detail = detailOf(home),
            status = home.status,
            shortLabel = shortLabelOf(home),
            progress = home.ringProgress,
            arrivalMinuteOfDay = home.arrivalMinuteOfDay.takeUnless { home.isVoluntaryDetour || home.hasWeakLocation }
        )
    }

    private fun titleOf(home: HomeUiState): StringRef = when {
        !home.isMonitoring -> StringRef(R.string.home_idle_title)
        home.hasWeakLocation -> StringRef(R.string.error_gps_what)
        home.isVoluntaryDetour -> StringRef(R.string.home_walking_safely)
        home.routineName != null -> StringRef(R.string.home_heading_to, home.routineName)
        home.isNewPlace -> StringRef(R.string.home_new_place)
        else -> StringRef(R.string.home_only_observing)
    }

    /** Una o dos palabras para la complicación, que tiene muy poco espacio. */
    private fun shortLabelOf(home: HomeUiState): Int = when {
        !home.isMonitoring -> R.string.complication_idle
        home.hasWeakLocation -> R.string.complication_no_gps
        home.status == StatusPresentation.HELP -> R.string.complication_help
        home.status == StatusPresentation.DEVIATION -> R.string.complication_deviation
        else -> R.string.complication_on_route
    }

    private fun detailOf(home: HomeUiState): StringRef = when {
        !home.isMonitoring -> StringRef(R.string.home_idle_body)
        home.hasWeakLocation -> StringRef(R.string.error_gps_todo)
        else -> StringRef(home.status.label)
    }
}
