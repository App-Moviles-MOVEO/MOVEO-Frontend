package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.CancelOutcome
import com.example.moveo_frontend.data.CarpoolBooking
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo
    private val carpoolRepo = ServiceLocator.carpoolingRepo

    private val _state = MutableStateFlow<UiState<List<Reservation>>>(UiState.Loading)
    val state = _state.asStateFlow()

    // Reservas de carpool (viajes compartidos), mostradas en su propia sección.
    private val _carpools = MutableStateFlow<List<CarpoolBooking>>(emptyList())
    val carpools = _carpools.asStateFlow()

    private val _detail = MutableStateFlow<UiState<Reservation>>(UiState.Idle)
    val detail = _detail.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.myReservations()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
            carpoolRepo.myBookings()
                .onSuccess { _carpools.value = it }
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

    // US54 + US26/US33: cancelación con política y reembolso automático.
    private val _cancelState = MutableStateFlow<UiState<CancelOutcome>>(UiState.Idle)
    val cancelState = _cancelState.asStateFlow()

    fun cancel(reservation: Reservation) {
        _cancelState.value = UiState.Loading
        viewModelScope.launch {
            repo.cancel(reservation)
                .onSuccess {
                    _cancelState.value = UiState.Success(it)
                    loadDetail(reservation.id) // refleja el estado "Cancelado"
                    load()
                }
                .onFailure { _cancelState.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetCancel() { _cancelState.value = UiState.Idle }

    // Avance del flujo de la reserva (aceptar → iniciar → finalizar). Al finalizar,
    // las fechas se liberan y el vehículo vuelve a estar disponible en el catálogo.
    private val _advanceState = MutableStateFlow<UiState<Reservation>>(UiState.Idle)
    val advanceState = _advanceState.asStateFlow()

    fun advance(reservationId: String, newStatus: String) {
        _advanceState.value = UiState.Loading
        viewModelScope.launch {
            repo.advanceStatus(reservationId, newStatus)
                .onSuccess {
                    _advanceState.value = UiState.Success(it)
                    _detail.value = UiState.Success(it)
                    load()
                }
                .onFailure { _advanceState.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetAdvance() { _advanceState.value = UiState.Idle }
}
