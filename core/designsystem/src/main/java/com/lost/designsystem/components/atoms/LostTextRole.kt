package com.lost.designsystem.components.atoms

import androidx.compose.ui.text.TextStyle
import com.lost.designsystem.theme.LostTypography

/** Roles tipográficos de la lámina "Átomos · Tipografía". */
enum class LostTextRole(val style: TextStyle, val maxLines: Int) {
    DISPLAY(LostTypography.display, maxLines = 1),
    NUMBER(LostTypography.number, maxLines = 1),
    QUESTION(LostTypography.question, maxLines = 3),
    TITLE(LostTypography.title, maxLines = 2),
    BODY(LostTypography.body, maxLines = 3),
    CAPTION(LostTypography.caption, maxLines = 2)
}
