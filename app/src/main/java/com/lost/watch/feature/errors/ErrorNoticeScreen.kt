package com.lost.watch.feature.errors

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.watch.R
import com.lost.watch.ui.rememberPermissionRequest

private val LOCATION_PERMISSIONS =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

/** Pantalla de error con su salida: dar el permiso de ubicación o "Entendido" en un aviso. */
@Composable
fun ErrorNoticeScreen(notice: ErrorNotice, onEvent: (ErrorUiEvent) -> Unit) {
    val requestLocation = rememberPermissionRequest(LOCATION_PERMISSIONS) { onEvent(ErrorUiEvent.Recheck) }
    when (notice.remedy) {
        ErrorRemedy.REQUEST_LOCATION -> ErrorScreen(
            notice.message,
            iconFor(notice.message),
            stringResource(R.string.error_permission_action),
            requestLocation
        )

        ErrorRemedy.ACKNOWLEDGE -> ErrorScreen(
            notice.message,
            iconFor(notice.message),
            stringResource(R.string.error_acknowledge)
        ) { onEvent(ErrorUiEvent.Acknowledge) }
    }
}
