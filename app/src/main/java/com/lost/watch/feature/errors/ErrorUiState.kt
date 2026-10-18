package com.lost.watch.feature.errors

import com.lost.watch.ui.UiMessage

/** Problemas del reloj que impiden acompañar bien (sin permiso, batería baja). Vacío si todo está bien. */
data class ErrorUiState(val messages: List<UiMessage>)
