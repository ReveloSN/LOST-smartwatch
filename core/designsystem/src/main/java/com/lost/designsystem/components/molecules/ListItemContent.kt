package com.lost.designsystem.components.molecules

import androidx.compose.ui.graphics.vector.ImageVector

/** Icono, título y detalle opcional de una fila de lista. */
data class ListItemContent(val icon: ImageVector, val title: String, val detail: String? = null)
