package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.remote.dto.PublishRouteRequest
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarpoolViewModel : ViewModel() {
    private val repo = ServiceLocator.carpoolingRepo
    private val session = ServiceLocator.session

    // US11: género declarado del usuario ("female" | "male" | "" sin declarar),
    // para validar la reserva en rutas exclusivas para mujeres.
    private val _gender = MutableStateFlow("")
    val gender = _gender.asStateFlow()

    private val _routes = MutableStateFlow<UiState<List<CarpoolRoute>>>(UiState.Loading)
    val routes = _routes.asStateFlow()

    private val _detail = MutableStateFlow<UiState<CarpoolRoute>>(UiState.Idle)
    val detail = _detail.asStateFlow()

    private val _publish = MutableStateFlow<UiState<CarpoolRoute>>(UiState.Idle)
    val publish = _publish.asStateFlow()

    private val _book = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val book = _book.asStateFlow()

    private val _onlyWomen = MutableStateFlow(false)
    val onlyWomen = _onlyWomen.asStateFlow()

    private val _onlyVerified = MutableStateFlow(true)
    val onlyVerified = _onlyVerified.asStateFlow()

    private val _community = MutableStateFlow<String?>(null)
    val community = _community.asStateFlow()

    init {
        load()
        viewModelScope.launch { _gender.value = session.genderBlocking() }
    }

    /** El usuario confirma que es mujer (rutas solo-mujeres); queda guardado. */
    fun confirmFemale() {
        _gender.value = "female"
        viewModelScope.launch { session.setGender("female") }
    }

    fun setOnlyWomen(v: Boolean) { _onlyWomen.value = v; load() }
    fun setOnlyVerified(v: Boolean) { _onlyVerified.value = v; load() }
    fun setCommunity(v: String?) { _community.value = v?.takeIf { it.isNotBlank() }; load() }

    fun load() {
        _routes.value = UiState.Loading
        viewModelScope.launch {
            repo.routes(_onlyWomen.value.takeIf { it }, _onlyVerified.value.takeIf { it }, _community.value)
                // No mostrar rutas sin cupos disponibles.
                .onSuccess { _routes.value = UiState.Success(it.filter { r -> r.seatsAvailable > 0 }) }
                .onFailure { _routes.value = UiState.Error(it.friendly()) }
        }
    }

    fun loadDetail(id: String) {
        _detail.value = UiState.Loading
        viewModelScope.launch {
            repo.route(id)
                .onSuccess { _detail.value = UiState.Success(it) }
                .onFailure { _detail.value = UiState.Error(it.friendly()) }
        }
    }

    fun publishRoute(req: PublishRouteRequest) {
        _publish.value = UiState.Loading
        viewModelScope.launch {
            repo.publish(req)
                .onSuccess { _publish.value = UiState.Success(it) }
                .onFailure { _publish.value = UiState.Error(it.friendly()) }
        }
    }

    fun book(route: CarpoolRoute, seats: Int = 1) {
        _book.value = UiState.Loading
        viewModelScope.launch {
            repo.book(route, seats)
                .onSuccess { _book.value = UiState.Success(Unit) }
                .onFailure { _book.value = UiState.Error(it.friendly()) }
        }
    }
}
