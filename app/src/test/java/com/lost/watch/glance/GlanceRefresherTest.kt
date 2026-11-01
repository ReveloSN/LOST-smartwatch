package com.lost.watch.glance

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSession
import com.lost.watch.AppFixtures
import com.lost.watch.feature.home.HomeUiStateMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GlanceRefresherTest {
    private val session = TripSession()
    private var updates = 0
    private val refresher = GlanceRefresher(session, GlanceSummaryMapper(HomeUiStateMapper())) { updates++ }

    @Test
    fun `pide redibujar al arrancar y luego solo cuando cambia algo que se ve`() = runTest {
        backgroundScope.launch { refresher.run() }
        runCurrent()
        assertEquals(1, updates)

        val calm = AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW))
        session.publish { calm }
        runCurrent()
        assertEquals(2, updates)

        // Un punto GPS más con el mismo estado y casi el mismo avance no cambia nada en pantalla.
        session.publish { calm.copy(detection = calm.detection.copy(routeProgress = 0.31)) }
        runCurrent()
        assertEquals(2, updates)

        session.publish { calm.copy(detection = calm.detection.copy(level = AssistanceLevel.MEDIUM)) }
        runCurrent()
        assertEquals(3, updates)
    }
}
