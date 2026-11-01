package com.lost.watch.tile

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography.BODY_MEDIUM
import androidx.wear.protolayout.material3.Typography.LABEL_MEDIUM
import androidx.wear.protolayout.material3.Typography.TITLE_LARGE
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.LayoutColor
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
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.session.TripSession
import com.lost.watch.MainActivity
import com.lost.watch.R
import com.lost.watch.glance.GlanceSummary
import com.lost.watch.glance.GlanceSummaryMapper
import com.lost.watch.glance.StringRef
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.formatMinuteOfDay
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val RESOURCES_VERSION = "0"
private const val OPEN_LOST_ID = "open_lost"

/**
 * 05.2 · Vista rápida: "LOST · ahora", la rutina ("En tu ruta Casa → U"), el estado en su color ("Todo en
 * orden") y la llegada en su propia línea ("Llegada 7:15"). Tocarla abre Inicio. Se actualiza en vivo
 * (`GlanceRefresher`).
 */
@AndroidEntryPoint
class MainTileService : TileService() {
    @Inject lateinit var session: TripSession

    @Inject lateinit var mapper: GlanceSummaryMapper

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        Futures.immediateFuture(tile(requestParams, this, mapper.map(session.snapshot.value)))

    override fun onTileResourcesRequest(requestParams: ResourcesRequest): ListenableFuture<Resources> =
        Futures.immediateFuture(resources())
}

private fun resources(): Resources = Resources.Builder()
    .setVersion(RESOURCES_VERSION)
    .build()

private fun tile(
    requestParams: RequestBuilders.TileRequest,
    context: Context,
    summary: GlanceSummary
): TileBuilders.Tile = TileBuilders.Tile.Builder()
    .setResourcesVersion(RESOURCES_VERSION)
    .setTileTimeline(
        TimelineBuilders.Timeline.fromLayoutElement(
            materialScope(context, requestParams.deviceConfiguration) {
                primaryLayout(
                    titleSlot = {
                        text(
                            context.getString(R.string.tile_title_now).layoutString,
                            typography = LABEL_MEDIUM,
                            color = LayoutColor(SemanticTokens.Text.secondary.toArgb())
                        )
                    },
                    mainSlot = { summaryColumn(context, summary) },
                    onClick = clickable(openLost(context), OPEN_LOST_ID)
                )
            }
        )
    )
    .build()

private fun MaterialScope.summaryColumn(context: Context, summary: GlanceSummary): LayoutElementBuilders.Column {
    val statusColor = LayoutColor(SemanticTokens.Status.of(summary.status.tone).toArgb())
    val titleColor = LayoutColor(SemanticTokens.Text.primary.toArgb())
    val title = summary.title.resolve(context).layoutString
    val detail = summary.detail.resolve(context).layoutString
    val column = LayoutElementBuilders.Column.Builder()
        .addContent(text(title, typography = TITLE_LARGE, color = titleColor, maxLines = 2))
        .addContent(text(detail, typography = BODY_MEDIUM, color = statusColor, maxLines = 2))
    arrivalLine(context, summary)?.let {
        column.addContent(text(it.layoutString, typography = BODY_MEDIUM, color = titleColor))
    }
    return column.build()
}

/** "Llegada 7:15" en su propia línea: es el dato que se busca de un vistazo. */
private fun arrivalLine(context: Context, summary: GlanceSummary): String? = summary.arrivalMinuteOfDay?.let {
    context.getString(R.string.glance_arrival, formatMinuteOfDay(it, context.resources.configuration.locales[0]))
}

private fun openLost(context: Context): ActionBuilders.LaunchAction = ActionBuilders.LaunchAction.Builder()
    .setAndroidActivity(
        ActionBuilders.AndroidActivity.Builder()
            .setPackageName(context.packageName)
            .setClassName(MainActivity::class.java.name)
            .build()
    )
    .build()

@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData({ resources() }) {
    val summary = GlanceSummary(
        isMonitoring = true,
        title = StringRef(R.string.glance_on_routine, "Casa → U"),
        detail = StringRef(R.string.status_on_route),
        status = StatusPresentation.ON_ROUTE,
        shortLabel = R.string.complication_on_route,
        progress = 0.5f,
        arrivalMinuteOfDay = 435
    )
    tile(it, context, summary)
}
