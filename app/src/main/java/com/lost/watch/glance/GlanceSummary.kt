package com.lost.watch.glance

import androidx.annotation.StringRes
import com.lost.watch.ui.StatusPresentation

/**
 * Lo que dicen la complicación y la vista rápida (05.1 y 05.2): a dónde vas o qué pasa, una línea de
 * estado, el avance del recorrido para el anillo y la hora de llegada si hay rutina.
 */
data class GlanceSummary(
    val isMonitoring: Boolean,
    val title: StringRef,
    val detail: StringRef,
    val status: StatusPresentation,
    @param:StringRes val shortLabel: Int,
    val progress: Float,
    val arrivalMinuteOfDay: Int?
)
