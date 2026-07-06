package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo

    private val _state = MutableStateFlow<UiState<Vehicle>>(UiState.Idle)
    val state = _state.asStateFlow()

    // Reseñas reales del vehículo (se muestran en el detalle antes de reservar).
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    fun load(id: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.vehicle(id)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
        viewModelScope.launch {
            repo.vehicleReviews(id).onSuccess { _reviews.value = it }
        }
    }
}
