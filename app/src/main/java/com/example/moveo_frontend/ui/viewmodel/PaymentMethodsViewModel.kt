package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentMethodsViewModel : ViewModel() {
    private val billing = ServiceLocator.billingRepo

    private val _methods = MutableStateFlow<UiState<List<PaymentMethodDto>>>(UiState.Loading)
    val methods = _methods.asStateFlow()

    private val items = mutableListOf<PaymentMethodDto>()

    init { load() }

    fun load() {
        _methods.value = UiState.Loading
        viewModelScope.launch {
            billing.methods()
                .onSuccess { items.clear(); items.addAll(it); emit() }
                .onFailure { _methods.value = UiState.Error(it.friendly()) }
        }
    }

    fun addCard(last4: String) {
        val digits = last4.takeLast(4).padStart(4, '*')
        items.add(PaymentMethodDto("pm_${System.currentTimeMillis()}", "Tarjeta de crédito", "Visa **** $digits"))
        emit()
    }

    fun remove(id: String) {
        items.removeAll { it.id == id }
        emit()
    }

    private fun emit() { _methods.value = UiState.Success(items.toList()) }
}