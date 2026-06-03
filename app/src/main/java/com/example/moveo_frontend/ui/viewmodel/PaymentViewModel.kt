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

    // Estado del pago completo (exito final / error / loading durante la preparacion).
    private val _payment = MutableStateFlow<UiState<PaymentResponse>>(UiState.Idle)
    val payment = _payment.asStateFlow()

    // client_secret listo -> la pantalla presenta el Stripe PaymentSheet. Se limpia tras presentarlo.
    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret = _clientSecret.asStateFlow()

    private var pendingReservationId: String? = null
    private var pendingMethod: String = "card"
    private var pendingAmount: Int = 0
    private var loadedVehicle: Vehicle? = null

    fun load(vehicleId: String) {
        _vehicle.value = UiState.Loading
        viewModelScope.launch {
            rental.vehicle(vehicleId)
                .onSuccess { loadedVehicle = it; _vehicle.value = UiState.Success(it) }
                .onFailure { _vehicle.value = UiState.Error(it.friendly()) }
            billing.methods()
                .onSuccess { _methods.value = it }
        }
    }

    /** Crea la reserva en el backend; al terminar emite el client_secret para abrir el sheet de Stripe. */
    fun preparePayment(vehicleId: String, method: String, amount: Int, startDate: String, endDate: String) {
        pendingMethod = method
        pendingAmount = amount
        _payment.value = UiState.Loading
        viewModelScope.launch {
            val reservation = createReservation(vehicleId, amount, startDate, endDate) ?: return@launch
            pendingReservationId = reservation.id
            billing.createPaymentIntent(amount.toLong() * 100, reservation.id)
                .onSuccess { _clientSecret.value = it }
                .onFailure { _payment.value = UiState.Error(it.friendly()) }
        }
    }

    /**
     * Pago rápido por Yape: crea la reserva y la paga en un paso (POST /rentals/{id}/pay).
     * No pasa por Stripe; ideal para culminar la transacción al instante.
     */
    fun payDirect(vehicleId: String, method: String, amount: Int, startDate: String, endDate: String) {
        pendingMethod = method
        pendingAmount = amount
        _payment.value = UiState.Loading
        viewModelScope.launch {
            val reservation = createReservation(vehicleId, amount, startDate, endDate) ?: return@launch
            pendingReservationId = reservation.id
            rental.pay(reservation.id, method, amount)
                .onSuccess { _payment.value = UiState.Success(it) }
                .onFailure { _payment.value = UiState.Error(it.friendly()) }
        }
    }

    /** Crea la reserva tomando ownerId/ubicación del vehículo cargado. Emite error en el StateFlow si falla. */
    private suspend fun createReservation(
        vehicleId: String, amount: Int, startDate: String, endDate: String
    ): com.example.moveo_frontend.data.Reservation? {
        val v = loadedVehicle
        if (v == null) {
            _payment.value = UiState.Error("Aún no se cargó el vehículo")
            return null
        }
        val result = rental.reserve(
            vehicleId = vehicleId,
            ownerId = v.ownerId,
            startDate = startDate,
            endDate = endDate,
            totalPrice = amount,
            pickupLocation = v.location
        )
        return result.getOrElse {
            _payment.value = UiState.Error(it.friendly())
            null
        }
    }

    /** La pantalla ya consumio el client_secret y abrio el sheet. */
    fun onSheetPresented() { _clientSecret.value = null }

    /** Stripe confirmo el cobro. Registramos el pago y marcamos exito. */
    fun onPaymentSucceeded() {
        viewModelScope.launch {
            pendingReservationId?.let { rental.pay(it, pendingMethod, pendingAmount) }
            _payment.value = UiState.Success(
                PaymentResponse(id = "paid_${System.currentTimeMillis()}", status = "success")
            )
        }
    }

    fun onPaymentCanceled() {
        if (_payment.value is UiState.Loading) _payment.value = UiState.Idle
    }

    fun onPaymentFailed(message: String) {
        _payment.value = UiState.Error(message)
    }
}