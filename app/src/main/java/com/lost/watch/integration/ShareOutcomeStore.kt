package com.lost.watch.integration

import com.lost.domain.error.LostError
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Último resultado de avisar al contacto: la capa de alerta muestra el error si no se pudo. */
@Singleton
class ShareOutcomeStore @Inject constructor() {
    private val error = MutableStateFlow<LostError?>(null)
    val lastError: StateFlow<LostError?> = error.asStateFlow()

    fun report(failure: LostError?) {
        error.value = failure
    }
}
