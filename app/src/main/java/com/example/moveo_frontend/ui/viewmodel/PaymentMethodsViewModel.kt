package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** US21: métodos de pago vinculados por el usuario, persistentes en el dispositivo. */
class PaymentMethodsViewModel : ViewModel() {
    private val store = ServiceLocator.paymentMethods

    private val _methods = MutableStateFlow<UiState<List<PaymentMethodDto>>>(UiState.Loading)
    val methods = _methods.asStateFlow()

    init {
        viewModelScope.launch {
            store.methods.collect { _methods.value = UiState.Success(it) }
        }
    }

    fun load() { /* el flujo del store ya es reactivo; no hace falta recargar */ }

    fun addCard(last4: String) {
        val digits = last4.takeLast(4).padStart(4, '*')
        viewModelScope.launch { store.add("Tarjeta de crédito", "Visa **** $digits") }
    }

    fun addWallet(provider: String, phone: String) {
        viewModelScope.launch { store.add(provider, "$provider · $phone") }
    }

    fun remove(id: String) {
        viewModelScope.launch { store.remove(id) }
    }
}
