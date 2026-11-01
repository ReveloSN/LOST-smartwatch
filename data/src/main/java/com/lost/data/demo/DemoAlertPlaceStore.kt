package com.lost.data.demo

import com.lost.domain.port.AlertPlaceReader
import com.lost.domain.port.AlertPlaceRecorder
import javax.inject.Inject

/** Leer y guardar lugares de alerta, juntos para el sembrador de la demo. */
class DemoAlertPlaceStore @Inject constructor(val reader: AlertPlaceReader, val recorder: AlertPlaceRecorder)
