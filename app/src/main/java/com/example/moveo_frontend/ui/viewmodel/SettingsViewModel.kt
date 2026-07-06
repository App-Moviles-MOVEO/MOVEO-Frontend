package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Acciones de cuenta desde Configuración: cambio de contraseña, baja voluntaria (US45)
 *  y solicitud de alianza corporativa (US46). */
class SettingsViewModel : ViewModel() {
    private val auth = ServiceLocator.authRepo
    private val ops = ServiceLocator.operationsRepo

    private val _changePassword = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val changePassword = _changePassword.asStateFlow()

    private val _deleteAccount = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteAccount = _deleteAccount.asStateFlow()

    fun changePassword(current: String, new: String, confirm: String) {
        when {
            current.isBlank() || new.isBlank() -> {
                _changePassword.value = UiState.Error("Completa todos los campos"); return
            }
            new.length < 6 -> {
                _changePassword.value = UiState.Error("La nueva contraseña debe tener al menos 6 caracteres"); return
            }
            new != confirm -> {
                _changePassword.value = UiState.Error("Las contraseñas no coinciden"); return
            }
        }
        _changePassword.value = UiState.Loading
        viewModelScope.launch {
            auth.changePassword(current, new)
                .onSuccess { _changePassword.value = UiState.Success(Unit) }
                .onFailure { _changePassword.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetChangePassword() { _changePassword.value = UiState.Idle }

    fun deleteAccount() {
        _deleteAccount.value = UiState.Loading
        viewModelScope.launch {
            auth.deleteAccount()
                .onSuccess { _deleteAccount.value = UiState.Success(Unit) }
                .onFailure { _deleteAccount.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetDeleteAccount() { _deleteAccount.value = UiState.Idle }

    private val _partnership = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val partnership = _partnership.asStateFlow()

    fun requestPartnership(company: String, email: String, message: String) {
        if (company.isBlank() || email.isBlank()) {
            _partnership.value = UiState.Error("Ingresa la empresa y un correo de contacto"); return
        }
        _partnership.value = UiState.Loading
        viewModelScope.launch {
            ops.requestCorporatePartnership(company, email, message)
                .onSuccess { _partnership.value = UiState.Success(Unit) }
                .onFailure { _partnership.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetPartnership() { _partnership.value = UiState.Idle }
}
