package com.lost.watch.tile

import android.content.Context
import androidx.annotation.StringRes
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.Typography.BODY_LARGE
import androidx.wear.protolayout.material3.Typography.TITLE_MEDIUM
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSession
import com.lost.watch.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"

/** 05.2 · Vista rápida: el estado de LOST en un vistazo ("LOST · Todo en orden"). */
@AndroidEntryPoint
class MainTileService : TileService() {
    @Inject lateinit var session: TripSession

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        Futures.immediateFuture(tile(requestParams, this, statusLabel()))

    override fun onTileResourcesRequest(requestParams: ResourcesRequest): ListenableFuture<Resources> =
        Futures.immediateFuture(resources())

    @StringRes
    private fun statusLabel(): Int {
        val snapshot = session.snapshot.value
        return when {
            !snapshot.isMonitoring -> R.string.home_idle_title
            snapshot.detection.level == AssistanceLevel.HIGH -> R.string.status_help
            snapshot.detection.level == AssistanceLevel.MEDIUM -> R.string.status_deviation
            else -> R.string.status_on_route
        }
    }
}

private fun resources(): Resources = Resources.Builder()
    .setVersion(RESOURCES_VERSION)
    .build()

private fun tile(
    requestParams: RequestBuilders.TileRequest,
    context: Context,
    @StringRes status: Int
): TileBuilders.Tile = TileBuilders.Tile.Builder()
    .setResourcesVersion(RESOURCES_VERSION)
    .setTileTimeline(
        TimelineBuilders.Timeline.fromLayoutElement(
            materialScope(context, requestParams.deviceConfiguration) {
                primaryLayout(
                    titleSlot = {
                        text(context.getString(R.string.tile_label).layoutString, typography = TITLE_MEDIUM)
                    },
                    mainSlot = { text(context.getString(status).layoutString, typography = BODY_LARGE) }
                )
            }
        )
    )
    .build()

@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData({ resources() }) {
    tile(it, context, R.string.status_on_route)
}
