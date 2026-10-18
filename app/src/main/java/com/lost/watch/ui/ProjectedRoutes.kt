package com.lost.watch.ui

import androidx.compose.ui.geometry.Offset

/** Rutas proyectadas a coordenadas 0..1 para dibujarlas en el mini-mapa. */
data class ProjectedRoutes(val habitual: List<Offset>, val today: List<Offset>, val divergence: Offset?)
