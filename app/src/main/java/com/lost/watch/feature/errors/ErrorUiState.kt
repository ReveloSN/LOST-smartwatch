package com.lost.watch.feature.errors

/** Problemas del reloj que impiden acompañar bien (sin permiso, batería baja). Vacío si todo está bien. */
data class ErrorUiState(val notices: List<ErrorNotice>)
