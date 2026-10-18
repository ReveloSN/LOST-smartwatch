package com.lost.designsystem.components.organisms

import com.lost.designsystem.components.molecules.RoutineCardContent

/** Un elemento del carrusel con su acción. */
data class RoutineCarouselItem(val key: String, val content: RoutineCardContent, val onClick: () -> Unit)
