package com.lost.watch.ui

import com.lost.domain.error.LostError
import com.lost.watch.R
import javax.inject.Inject

/** Traduce errores del dominio al lenguaje del usuario ("GPS signal lost" → "No encontramos tu ubicación"). */
class LostErrorToUiMessageMapper @Inject constructor() {
    fun map(error: LostError): UiMessage = when (error) {
        LostError.LocationUnavailable ->
            UiMessage(R.string.error_gps_what, R.string.error_gps_why, R.string.error_gps_todo)

        LostError.NoConnection ->
            UiMessage(R.string.error_connection_what, R.string.error_connection_why, R.string.error_connection_todo)

        is LostError.BatteryLow ->
            UiMessage(R.string.error_battery_what, null, R.string.error_battery_todo, error.percent)

        LostError.LocationPermissionDenied ->
            UiMessage(R.string.error_permission_what, R.string.error_permission_why, R.string.error_permission_todo)

        LostError.NoAuthorizedContact ->
            UiMessage(R.string.error_no_contact_what, null, R.string.error_no_contact_todo)

        LostError.NoRoutinesYet ->
            UiMessage(R.string.error_no_routines_what, null, R.string.error_no_routines_todo)

        is LostError.StorageFailure ->
            UiMessage(R.string.error_storage_what, null, R.string.error_storage_todo)

        LostError.MapUnavailable ->
            UiMessage(R.string.error_map_what, R.string.error_connection_why, R.string.error_map_todo)

        LostError.SearchUnavailable ->
            UiMessage(R.string.error_search_what, R.string.error_connection_why, R.string.error_search_todo)
    }
}
