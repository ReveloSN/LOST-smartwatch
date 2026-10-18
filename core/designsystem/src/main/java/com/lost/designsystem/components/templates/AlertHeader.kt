package com.lost.designsystem.components.templates

import androidx.compose.ui.graphics.vector.ImageVector
import com.lost.designsystem.tokens.StatusTone

/** Encabezado de una alerta: icono, contexto corto y pregunta principal. */
data class AlertHeader(val icon: ImageVector, val context: String?, val question: String, val tone: StatusTone)
