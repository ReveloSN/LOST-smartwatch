package com.lost.designsystem.components.organisms

import androidx.compose.ui.graphics.vector.ImageVector
import com.lost.designsystem.tokens.StatusTone

/** Icono + título + mensaje de una alerta, con su tono. */
data class AlertContent(val icon: ImageVector, val title: String, val message: String?, val tone: StatusTone)
