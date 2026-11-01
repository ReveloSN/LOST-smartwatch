package com.lost.watch.glance

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.AppFixtures
import com.lost.watch.R
import com.lost.watch.feature.home.HomeUiStateMapper
import com.lost.watch.ui.StatusPresentation
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GlanceSummaryMapperTest {
    private val mapper = GlanceSummaryMapper(HomeUiStateMapper())

    @Test
    fun `sin recorrido dice que lost esta atento`() {
        val summary = mapper.map(TripSessionSnapshot.IDLE)
        assertFalse(summary.isMonitoring)
        assertEquals(StringRef(R.string.home_idle_title), summary.title)
        assertEquals(R.string.complication_idle, summary.shortLabel)
        assertNull(summary.arrivalMinuteOfDay)
    }

    @Test
    fun `en rutina dice a donde vas con el avance y la llegada como inicio`() {
        val summary = mapper.map(AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)), ZoneOffset.UTC)
        assertEquals(StringRef(R.string.home_heading_to, "Casa → U"), summary.title)
        assertEquals(StringRef(R.string.status_on_route), summary.detail)
        assertEquals(0.3f, summary.progress)
        assertEquals(7 * 60 + 15, summary.arrivalMinuteOfDay)
        assertEquals(R.string.complication_on_route, summary.shortLabel)
    }

    @Test
    fun `con posible desorientacion la palabra corta lo dice`() {
        val summary = mapper.map(AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.MEDIUM)))
        assertEquals(StatusPresentation.DEVIATION, summary.status)
        assertEquals(R.string.complication_deviation, summary.shortLabel)
    }

    @Test
    fun `sin ubicacion confiable no muestra una llegada que no puede calcular`() {
        val snapshot = AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)).copy(hasWeakLocation = true)
        val summary = mapper.map(snapshot)
        assertEquals(StringRef(R.string.error_gps_what), summary.title)
        assertEquals(R.string.complication_no_gps, summary.shortLabel)
        assertNull(summary.arrivalMinuteOfDay)
    }

    @Test
    fun `tras voy a otro lado no promete la llegada de la rutina`() {
        val snapshot = AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)).copy(isGoingElsewhere = true)
        val summary = mapper.map(snapshot)
        assertEquals(StringRef(R.string.home_walking_safely), summary.title)
        assertNull(summary.arrivalMinuteOfDay)
    }
}
