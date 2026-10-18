package com.lost.data.demo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

/** Abre los GPX con el `AssetManager` de Android. */
class AndroidGpxAssetOpener @Inject constructor(@ApplicationContext private val context: Context) : GpxAssetOpener {
    override fun open(assetName: String): InputStream = context.assets.open(assetName)
}
