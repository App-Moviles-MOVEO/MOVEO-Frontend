package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.data.remote.dto.PaymentResponse
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val rental = ServiceLocator.rentalRepo
    private val billing = ServiceLocator.billingRepo

    private val _vehicle = MutableStateFlow<UiState<Vehicle>>(UiState.Loading)
    val vehicle = _vehicle.asStateFlow()

    private val _methods = MutableStateFlow<List<PaymentMethodDto>>(emptyList())
    val methods = _methods.asStateFlow()

    private val _payment = MutableStateFlow<UiState<PaymentResponse>>(UiState.Idle)
    val payment = _payment.asStateFlow()

    fun load(vehicleId: String) {
        _vehicle.value = UiState.Loading
        viewModelScope.launch {
            rental.vehicle(vehicleId)
                .onSuccess { _vehicle.value = UiState.Success(it) }
                .onFailure { _vehicle.value = UiState.Error(it.friendly()) }
            billing.methods()
                .onSuccess { _methods.value = it }
        }
    }

    fun pay(vehicleId: String, method: String, amount: Int, startDate: String, endDate: String) {
        _payment.value = UiState.Loading
        viewModelScope.launch {
            val reservation = rental.reserve(vehicleId, startDate, endDate).getOrNull()
            if (reservation == null) {
                _payment.value = UiState.Error("No se pudo crear la reserva")
                return@launch
            }
            billing.pay(reservation.id, method, amount)
                .onSuccess { _payment.value = UiState.Success(it) }
                .onFailure { _payment.value = UiState.Error(it.friendly()) }
        }
    }
}
