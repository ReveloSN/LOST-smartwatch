package com.lost.data.demo

import com.lost.data.BuildConfig

/** ¿Está activo el modo demo? (compilaciones debug). */
object DemoMode {
    val isEnabled: Boolean = BuildConfig.DEMO_MODE
}
