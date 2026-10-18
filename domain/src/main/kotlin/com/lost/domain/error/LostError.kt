package com.lost.domain.error

/**
 * Errores conocidos de LOST. Nunca llegan como excepción a la UI: la UI los convierte
 * en un mensaje de tres partes (qué pasó · por qué · qué hacer).
 */
sealed interface LostError {
    data object LocationUnavailable : LostError

    data object NoConnection : LostError

    data class BatteryLow(val percent: Int) : LostError

    data object NoAuthorizedContact : LostError

    data object LocationPermissionDenied : LostError

    data object NoRoutinesYet : LostError

    data class StorageFailure(val detail: String) : LostError
}
