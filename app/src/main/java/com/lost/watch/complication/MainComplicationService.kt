package com.lost.watch.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSession
import com.lost.watch.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** 05.1 · Complicación de esfera: el estado de LOST en una palabra ("En tu ruta"). Tocar abre Inicio. */
@AndroidEntryPoint
class MainComplicationService : SuspendingComplicationDataSourceService() {
    @Inject lateinit var session: TripSession

    override fun getPreviewData(type: ComplicationType): ComplicationData? =
        if (type == ComplicationType.SHORT_TEXT) shortText(R.string.complication_on_route) else null

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val snapshot = session.snapshot.value
        val label = when {
            !snapshot.isMonitoring -> R.string.complication_idle
            snapshot.detection.level == AssistanceLevel.HIGH -> R.string.complication_help
            snapshot.detection.level == AssistanceLevel.MEDIUM -> R.string.complication_deviation
            else -> R.string.complication_on_route
        }
        return shortText(label)
    }

    private fun shortText(label: Int): ShortTextComplicationData = ShortTextComplicationData.Builder(
        text = PlainComplicationText.Builder(getString(label)).build(),
        contentDescription = PlainComplicationText.Builder(getString(R.string.complication_label)).build()
    ).build()
}
