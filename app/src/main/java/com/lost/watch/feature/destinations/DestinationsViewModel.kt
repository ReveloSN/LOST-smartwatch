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
import kotlinx.coroutines.launch

@HiltViewModel
class DestinationsViewModel @Inject constructor(
    observeDestinations: ObserveDestinationsUseCase,
    private val voiceSearch: DestinationVoiceSearch
) : ViewModel() {
    private val search = MutableStateFlow(DestinationSearchState())
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

    val uiState: StateFlow<DestinationsUiState> = combine(items, search, ::DestinationsUiState).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        DestinationsUiState(emptyList(), DestinationSearchState())
    )

    fun onEvent(event: DestinationsUiEvent) {
        when (event) {
            is DestinationsUiEvent.VoiceQuery -> onVoiceQuery(event.spokenText)

            is DestinationsUiEvent.ChoosePlace -> viewModelScope.launch {
                search.value = voiceSearch.choose(event.place)
            }

            DestinationsUiEvent.MatchConsumed -> search.value = DestinationSearchState()
        }
    }

    private fun onVoiceQuery(spokenText: String) {
        voiceSearch.matchSaved(spokenText, items.value)?.let {
            search.value = it
            return
        }
        search.value = DestinationSearchState(isSearching = true)
        viewModelScope.launch { search.value = voiceSearch.searchOnline(spokenText) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
