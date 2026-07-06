package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = ServiceLocator.authRepo

    private val _login = MutableStateFlow<UiState<User>>(UiState.Idle)
    val login = _login.asStateFlow()

    private val _register = MutableStateFlow<UiState<User>>(UiState.Idle)
    val register = _register.asStateFlow()

    // Success.data = resetToken (no null solo en modo desarrollo del backend).
    private val _forgot = MutableStateFlow<UiState<String?>>(UiState.Idle)
    val forgot = _forgot.asStateFlow()

    private val _reset = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val reset = _reset.asStateFlow()

    fun doLogin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _login.value = UiState.Error("Completa email y contraseña")
            return
        }
        _login.value = UiState.Loading
        viewModelScope.launch {
            repo.login(email.trim(), password)
                .onSuccess { _login.value = UiState.Success(it) }
                .onFailure { _login.value = UiState.Error(it.friendly()) }
        }
    }

    fun doRegister(name: String, email: String, phone: String, password: String, role: String, gender: String = "") {
        if (listOf(name, email, phone, password).any { it.isBlank() }) {
            _register.value = UiState.Error("Completa todos los campos")
            return
        }
        _register.value = UiState.Loading
        viewModelScope.launch {
            repo.register(name.trim(), email.trim(), phone.trim(), password, role, gender)
                .onSuccess { _register.value = UiState.Success(it) }
                .onFailure { _register.value = UiState.Error(it.friendly()) }
        }
    }

    fun doForgot(email: String) {
        if (email.isBlank()) {
            _forgot.value = UiState.Error("Ingresa tu correo")
            return
        }
        _forgot.value = UiState.Loading
        viewModelScope.launch {
            repo.forgotPassword(email.trim())
                .onSuccess { _forgot.value = UiState.Success(it) }
                .onFailure { _forgot.value = UiState.Error(it.friendly()) }
        }
    }

    fun doReset(token: String, newPassword: String) {
        if (token.isBlank() || newPassword.isBlank()) {
            _reset.value = UiState.Error("Ingresa el código y la nueva contraseña")
            return
        }
        _reset.value = UiState.Loading
        viewModelScope.launch {
            repo.resetPassword(token.trim(), newPassword)
                .onSuccess { _reset.value = UiState.Success(Unit) }
                .onFailure { _reset.value = UiState.Error(it.friendly()) }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.logout()
            onDone()
        }
    }
}
