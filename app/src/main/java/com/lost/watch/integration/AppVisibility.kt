package com.lost.watch.integration

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Si la app está a la vista; lo actualiza `MainActivity` al empezar y al dejar de verse. */
@Singleton
class AppVisibility @Inject constructor() {
    private val visible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = visible.asStateFlow()

    fun update(isVisible: Boolean) {
        visible.value = isVisible
    }
}
