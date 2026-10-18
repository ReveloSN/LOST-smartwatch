package com.lost.watch.feature.destinations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ObserveDestinationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DestinationsViewModel @Inject constructor(
    observeDestinations: ObserveDestinationsUseCase,
    private val matcher: DestinationMatcher
) : ViewModel() {
    private val voice = MutableStateFlow(VoiceResult(null, null))
    private val items = observeDestinations().map { destinations ->
        destinations.mapIndexed { index, destination ->
            DestinationItem(
                id = destination.id,
                name = destination.name,
                kind = destination.kind,
                isSuggested = index == 0 && destination.lastUsedAtMillis == null,
                isRecent = destination.lastUsedAtMillis != null
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val uiState: StateFlow<DestinationsUiState> = combine(items, voice) { list, result ->
        DestinationsUiState(list, result.notFound, result.matchedId)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        DestinationsUiState(emptyList(), null, null)
    )

    fun onEvent(event: DestinationsUiEvent) {
        voice.value = when (event) {
            is DestinationsUiEvent.VoiceQuery -> matcher.match(event.spokenText, items.value)
                ?.let { VoiceResult(it.id, null) } ?: VoiceResult(null, event.spokenText)

            DestinationsUiEvent.MatchConsumed -> VoiceResult(null, null)
        }
    }

    private data class VoiceResult(val matchedId: String?, val notFound: String?)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
