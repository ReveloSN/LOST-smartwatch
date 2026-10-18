package com.lost.data.demo

import java.io.InputStream

/** Abre un GPX empaquetado como asset (vienen de la carpeta `sample-routes/`). */
fun interface GpxAssetOpener {
    fun open(assetName: String): InputStream
}
