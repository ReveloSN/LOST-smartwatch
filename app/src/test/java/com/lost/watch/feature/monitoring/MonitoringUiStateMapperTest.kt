package com.lost.watch.feature.monitoring

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.port.ContactReader
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.AppFixtures
import com.lost.watch.ui.StatusPresentation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonitoringUiStateMapperTest {
    private val mapper = MonitoringUiStateMapper()

    @Test
    fun `sin recorrido no hay nada que comparar`() {
        assertEquals(MonitoringHeadline.IDLE, mapper.map(TripSessionSnapshot.IDLE).headline)
    }

    @Test
    fun `en la ruta dice que va por su ruta de siempre`() {
        val state = mapper.map(AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)))
        assertEquals(MonitoringHeadline.ON_ROUTE, state.headline)
        assertEquals(3, state.routes.habitual.size)
        assertEquals(2, state.routes.today.size)
    }

    @Test
    fun `apartandose sin confusion dice te estas apartando`() {
        val state = mapper.map(AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW, AppFixtures.reasons)))
        assertEquals(MonitoringHeadline.DRIFTING, state.headline)
    }

    @Test
    fun `desvio voluntario dice hoy tomaste otra calle`() {
        val detection = AppFixtures.detection(AssistanceLevel.LOW, AppFixtures.reasons, voluntary = true)
        assertEquals(MonitoringHeadline.OTHER_STREET, mapper.map(AppFixtures.monitoring(detection)).headline)
    }

    @Test
    fun `sin ubicacion confiable lo dice en vez del ultimo estado`() {
        val snapshot = AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.LOW)).copy(hasWeakLocation = true)
        assertEquals(MonitoringHeadline.NO_LOCATION, MonitoringUiStateMapper().map(snapshot).headline)
    }

    @Test
    fun `en la pausa tras estoy bien no muestra ayuda`() {
        val snapshot = AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.HIGH), AssistanceState.Paused)
        assertEquals(StatusPresentation.DEVIATION, MonitoringUiStateMapper().map(snapshot).status)
    }

    @Test
    fun `nivel medio dice que el recorrido ya no se parece`() {
        val state = mapper.map(
            AppFixtures.monitoring(AppFixtures.detection(AssistanceLevel.MEDIUM, AppFixtures.reasons))
        )
        assertEquals(MonitoringHeadline.NOT_SIMILAR, state.headline)
        assertEquals(AppFixtures.reasons, state.reasons)
    }
}
