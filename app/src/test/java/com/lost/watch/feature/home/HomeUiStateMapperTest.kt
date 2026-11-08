package com.lost.watch.feature.home

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.port.ContactReader
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.AppFixtures
import com.lost.watch.ui.StatusPresentation
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class HomeUiStateMapperTest {
    private val mapper = HomeUiStateMapper()

    @Test
    fun `sin recorrido muestra el estado tranquilo`() {
        val state = mapper.map(TripSessionSnapshot.IDLE, isDemoAvailable = true)
        assertFalse(state.isMonitoring)
        assertEquals(StatusPresentation.ON_ROUTE, state.status)
    }

    @Test
    fun `en rutina calcula la llegada y el avance en el anillo`() {
        val state = mapper.map(
            AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)),
            false,
            ZoneOffset.UTC
        )
        assertEquals("Casa → U", state.routineName)
        assertEquals(7 * 60 + 15, state.arrivalMinuteOfDay)
        assertEquals(0.3f, state.ringProgress)
    }

    @Test
    fun `nivel medio se ve como posible desorientacion`() {
        val state = mapper.map(AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.MEDIUM)), false)
        assertEquals(StatusPresentation.DEVIATION, state.status)
    }
}
