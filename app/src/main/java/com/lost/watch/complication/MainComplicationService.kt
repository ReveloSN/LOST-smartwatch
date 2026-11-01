package com.lost.watch.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.lost.domain.session.TripSession
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.R
import com.lost.watch.glance.GlanceSummary
import com.lost.watch.glance.GlanceSummaryMapper
import com.lost.watch.glance.openLostIntent
import com.lost.watch.ui.formatMinuteOfDay
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 05.1 · Complicación de esfera: el anillo con el avance del recorrido y la hora de llegada ("7:15"), o el
 * estado en una palabra ("En tu ruta") cuando no hay llegada. Tocarla abre Inicio. Se actualiza en vivo
 * (`GlanceRefresher`).
 */
@AndroidEntryPoint
class MainComplicationService : SuspendingComplicationDataSourceService() {
    @Inject lateinit var session: TripSession

    @Inject lateinit var mapper: GlanceSummaryMapper

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val preview = mapper.map(TripSessionSnapshot.IDLE)
        return dataFor(type, preview.copy(progress = PREVIEW_PROGRESS, shortLabel = R.string.complication_on_route))
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? =
        dataFor(request.complicationType, mapper.map(session.snapshot.value))

    private fun dataFor(type: ComplicationType, summary: GlanceSummary): ComplicationData? = when (type) {
        ComplicationType.RANGED_VALUE -> rangedValue(summary)
        ComplicationType.SHORT_TEXT -> shortText(summary)
        else -> null
    }

    private fun rangedValue(summary: GlanceSummary) = RangedValueComplicationData.Builder(
        value = summary.progress,
        min = 0f,
        max = 1f,
        contentDescription = description(summary)
    ).setText(mainText(summary))
        .setTitle(plain(getString(summary.shortLabel)).takeIf { summary.arrivalMinuteOfDay != null })
        .setTapAction(openLostIntent(this))
        .build()

    private fun shortText(summary: GlanceSummary) = ShortTextComplicationData.Builder(
        text = mainText(summary),
        contentDescription = description(summary)
    ).setTitle(plain(getString(summary.shortLabel)).takeIf { summary.arrivalMinuteOfDay != null })
        .setTapAction(openLostIntent(this))
        .build()

    /** La hora de llegada si se conoce; si no, el estado en una palabra. */
    private fun mainText(summary: GlanceSummary): PlainComplicationText = plain(
        summary.arrivalMinuteOfDay?.let { formatMinuteOfDay(it, resources.configuration.locales[0]) }
            ?: getString(summary.shortLabel)
    )

    private fun description(summary: GlanceSummary) =
        plain("${getString(R.string.complication_label)}: ${summary.title.resolve(this)}")

    private fun plain(text: String) = PlainComplicationText.Builder(text).build()

    private companion object {
        const val PREVIEW_PROGRESS = 0.5f
    }
}
