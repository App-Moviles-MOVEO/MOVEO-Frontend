package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyListingsViewModel : ViewModel() {
    private val rental = ServiceLocator.rentalRepo
    private val carpool = ServiceLocator.carpoolingRepo

    private val _vehicles = MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val vehicles = _vehicles.asStateFlow()

    private val _routes = MutableStateFlow<UiState<List<CarpoolRoute>>>(UiState.Loading)
    val routes = _routes.asStateFlow()

    init { load() }

    fun load() {
        _vehicles.value = UiState.Loading
        _routes.value = UiState.Loading
        viewModelScope.launch {
            rental.myVehicles()
                .onSuccess { _vehicles.value = UiState.Success(it) }
                .onFailure { _vehicles.value = UiState.Error(it.friendly()) }
            carpool.myRoutes()
                .onSuccess { _routes.value = UiState.Success(it) }
                .onFailure { _routes.value = UiState.Error(it.friendly()) }
        }
    }
}