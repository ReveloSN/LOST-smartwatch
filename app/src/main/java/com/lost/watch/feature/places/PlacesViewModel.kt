package com.lost.watch.feature.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.GetDisorientationPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlacesViewModel @Inject constructor(private val getPlaces: GetDisorientationPlacesUseCase) : ViewModel() {
    private val state = MutableStateFlow(PlacesUiState.LOADING)
    val uiState: StateFlow<PlacesUiState> = state.asStateFlow()

    init {
        onEvent(PlacesUiEvent.Refresh)
    }

    fun onEvent(event: PlacesUiEvent) {
        when (event) {
            PlacesUiEvent.Refresh -> viewModelScope.launch {
                state.value = PlacesUiState(isLoading = false, places = getPlaces())
            }
        }
    }
}
