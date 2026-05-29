package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehiclesViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo

    private val _state = MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _filter = MutableStateFlow("Todos")
    val filter = _filter.asStateFlow()

    init { load() }

    fun setFilter(f: String) {
        _filter.value = f
        load()
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val type = if (_filter.value == "Todos") null else _filter.value
            repo.vehicles(type = type)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }
}
