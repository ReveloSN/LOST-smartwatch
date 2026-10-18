package com.lost.ai.mode

import com.lost.ai.scoring.AnomalyScore

/** Lo que devuelve un modo de detección: el puntaje y el avance sobre la ruta (0 sin rutina). */
data class ModeEvaluation(val score: AnomalyScore, val progress: Double)
