package com.lost.watch.glance

import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.lost.watch.complication.MainComplicationService
import com.lost.watch.tile.MainTileService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Pide a Wear OS que vuelva a pedir los datos de la complicación y de la vista rápida de LOST. */
class WearGlanceSurfaces @Inject constructor(@ApplicationContext private val context: Context) : GlanceSurfaces {
    override fun requestUpdate() {
        ComplicationDataSourceUpdateRequester
            .create(context, ComponentName(context, MainComplicationService::class.java))
            .requestUpdateAll()
        TileService.getUpdater(context).requestUpdate(MainTileService::class.java)
    }
}
