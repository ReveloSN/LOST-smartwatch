package com.lost.domain.usecase

import com.lost.domain.port.CompassHeadingSource
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * La brújula solo se escucha mientras hace falta (cuando LOST está guiando): así el sensor
 * no gasta batería el resto del tiempo. `null` = sin dato todavía o sin brújula.
 */
class ObserveCompassHeadingUseCase @Inject constructor(private val source: CompassHeadingSource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(isNeeded: Flow<Boolean>): Flow<Double?> =
        isNeeded.distinctUntilChanged().flatMapLatest { needed ->
            if (needed) source.headings().map<Double, Double?> { it }.onStart { emit(null) } else flowOf(null)
        }
}
