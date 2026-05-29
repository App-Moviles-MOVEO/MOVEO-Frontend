package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel : ViewModel() {
    private val repo = ServiceLocator.carpoolingRepo

    private val _points = MutableStateFlow<List<TrackingPointDto>>(emptyList())
    val points = _points.asStateFlow()

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(routeId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.tracking(routeId)
                .onSuccess { _points.value = it; _state.value = UiState.Success(Unit) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }
}
