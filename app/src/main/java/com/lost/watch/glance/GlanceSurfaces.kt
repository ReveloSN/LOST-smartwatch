package com.lost.watch.glance

/** Las superficies fuera de la app (complicación de esfera y vista rápida) a las que se les pide redibujarse. */
fun interface GlanceSurfaces {
    fun requestUpdate()
}
