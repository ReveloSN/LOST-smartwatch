package com.lost.designsystem.components.atoms

import androidx.compose.ui.text.TextStyle
import com.lost.designsystem.theme.LostTypography
import kotlin.math.ceil

/**
 * Roles tipográficos de la lámina "Átomos · Tipografía". Títulos y textos de apoyo admiten 3 líneas:
 * en español caben en 1–2, pero en pt, en la pseudolocalización en-XA o con texto largo no deben cortarse.
 */
enum class LostTextRole(val style: TextStyle, val maxLines: Int) {
    DISPLAY(LostTypography.display, maxLines = 1),
    NUMBER(LostTypography.number, maxLines = 1),
    QUESTION(LostTypography.question, maxLines = 3),
    TITLE(LostTypography.title, maxLines = 3),
    BODY(LostTypography.body, maxLines = 3),
    CAPTION(LostTypography.caption, maxLines = 3);

    /**
     * Con texto grande el mismo texto ocupa más líneas: se dan las que hacen falta en vez de cortar
     * con "…" (las pantallas se desplazan). Los roles de una sola línea (números, display) no crecen.
     */
    fun maxLinesAt(fontScale: Float): Int =
        if (maxLines == 1) 1 else maxOf(maxLines, ceil(maxLines * fontScale).toInt())
}
