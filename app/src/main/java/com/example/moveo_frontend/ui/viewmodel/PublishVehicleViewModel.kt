package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublishVehicleViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo

    private val _state = MutableStateFlow<UiState<Vehicle>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun publish(req: PublishVehicleRequest) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.publish(req)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }
}
