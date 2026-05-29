package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo

    private val _state = MutableStateFlow<UiState<List<Reservation>>>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _detail = MutableStateFlow<UiState<Reservation>>(UiState.Idle)
    val detail = _detail.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.myReservations()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }

    fun loadDetail(id: String) {
        _detail.value = UiState.Loading
        viewModelScope.launch {
            repo.reservation(id)
                .onSuccess { _detail.value = UiState.Success(it) }
                .onFailure { _detail.value = UiState.Error(it.friendly()) }
        }
    }
}
