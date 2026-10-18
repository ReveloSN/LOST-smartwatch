package com.lost.watch.ui

import androidx.annotation.StringRes
import com.lost.domain.assistance.HapticPattern
import com.lost.watch.R

/**
 * Tono de la voz de LOST. La voz es fija (cercana, tranquila, en "tú"); el tono cambia
 * con el nivel: relajado, atento o firme. Cada tono define su texto principal y su vibración.
 */
enum class Tone(@param:StringRes val question: Int, @param:StringRes val context: Int?, val haptic: HapticPattern) {
    RELAXED(R.string.assist_soft_ask_title, null, HapticPattern.GENTLE),
    ATTENTIVE(R.string.assist_need_help, R.string.assist_left_route, HapticPattern.DOUBLE),
    FIRM(R.string.status_help, R.string.status_help_detail, HapticPattern.INSISTENT)
}
