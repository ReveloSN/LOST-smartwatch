package com.lost.watch.ui

import com.lost.domain.error.LostError
import com.lost.watch.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LostErrorToUiMessageMapperTest {
    private val mapper = LostErrorToUiMessageMapper()

    @Test
    fun `sin gps dice que paso por que y que hacer`() {
        val message = mapper.map(LostError.LocationUnavailable)
        assertEquals(R.string.error_gps_what, message.whatHappened)
        assertEquals(R.string.error_gps_why, message.why)
        assertEquals(R.string.error_gps_todo, message.whatToDo)
    }

    @Test
    fun `bateria baja lleva el porcentaje y omite el por que`() {
        val message = mapper.map(LostError.BatteryLow(10))
        assertNull(message.why)
        assertEquals(10, message.whatHappenedArgument)
    }

    @Test
    fun `todos los errores tienen mensaje humano`() {
        val errors = listOf(
            LostError.LocationUnavailable,
            LostError.NoConnection,
            LostError.BatteryLow(5),
            LostError.NoAuthorizedContact,
            LostError.LocationPermissionDenied,
            LostError.NoRoutinesYet,
            LostError.StorageFailure("disk")
        )
        errors.forEach { assertEquals(true, mapper.map(it).whatToDo != 0) }
    }
}
