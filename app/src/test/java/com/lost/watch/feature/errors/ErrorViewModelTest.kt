package com.lost.watch.feature.errors

import com.lost.domain.port.DeviceStatusReader
import com.lost.watch.R
import com.lost.watch.ui.LostErrorToUiMessageMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Ninguna pantalla de error de Inicio queda sin salida: cada aviso trae su acción y se vuelve a revisar. */
class ErrorViewModelTest {
    private val device = FakeDeviceStatus()

    private fun viewModel() = ErrorViewModel(device, LostErrorToUiMessageMapper())

    @Test
    fun `sin permiso de ubicacion ofrece pedirlo`() {
        device.hasPermission = false
        val notice = viewModel().uiState.value.notices.single()
        assertEquals(R.string.error_permission_what, notice.message.whatHappened)
        assertEquals(ErrorRemedy.REQUEST_LOCATION, notice.remedy)
    }

    @Test
    fun `al volver con el permiso dado la pantalla de error desaparece`() {
        device.hasPermission = false
        val viewModel = viewModel()
        device.hasPermission = true
        viewModel.onEvent(ErrorUiEvent.Recheck)
        assertTrue(viewModel.uiState.value.notices.isEmpty())
    }

    @Test
    fun `la bateria baja es un aviso que se cierra con entendido`() {
        device.battery = LOW_BATTERY
        val viewModel = viewModel()
        assertEquals(ErrorRemedy.ACKNOWLEDGE, viewModel.uiState.value.notices.single().remedy)
        viewModel.onEvent(ErrorUiEvent.Acknowledge)
        viewModel.onEvent(ErrorUiEvent.Recheck)
        assertTrue(viewModel.uiState.value.notices.isEmpty())
    }

    @Test
    fun `entendido en la bateria no oculta la falta de permiso`() {
        device.hasPermission = false
        device.battery = LOW_BATTERY
        val viewModel = viewModel()
        viewModel.onEvent(ErrorUiEvent.Acknowledge)
        assertEquals(ErrorRemedy.REQUEST_LOCATION, viewModel.uiState.value.notices.single().remedy)
    }

    private class FakeDeviceStatus : DeviceStatusReader {
        var hasPermission = true
        var battery: Int? = FULL_BATTERY

        override fun batteryPercent(): Int? = battery

        override fun hasLocationPermission(): Boolean = hasPermission
    }

    private companion object {
        const val LOW_BATTERY = 10
        const val FULL_BATTERY = 90
    }
}
