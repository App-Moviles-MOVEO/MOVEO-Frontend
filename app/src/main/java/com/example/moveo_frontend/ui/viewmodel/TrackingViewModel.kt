package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel : ViewModel() {
    private val repo = ServiceLocator.carpoolingRepo
    private val rentalRepo = ServiceLocator.rentalRepo

    private val _points = MutableStateFlow<List<TrackingPointDto>>(emptyList())
    val points = _points.asStateFlow()

    // US06: posición "en vivo" del vehículo. El backend no expone tracking, así que
    // se interpola la ruta y se avanza en tiempo real para simular el GPS.
    private val _current = MutableStateFlow<TrackingPointDto?>(null)
    val current = _current.asStateFlow()

    private val _progress = MutableStateFlow(0f) // 0..1 del recorrido
    val progress = _progress.asStateFlow()

    private val _etaMinutes = MutableStateFlow<Int?>(null)
    val etaMinutes = _etaMinutes.asStateFlow()

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state = _state.asStateFlow()

    // US20: resultado de confirmar la llegada (PATCH /rentals/{id} → completed).
    private val _completeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val completeState = _completeState.asStateFlow()

    private var simulation: Job? = null

    fun load(routeId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.tracking(routeId)
                .onSuccess {
                    _points.value = it
                    _state.value = UiState.Success(Unit)
                    startSimulation(it)
                }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }

    /** Avanza la posición por la ruta interpolada cada STEP_MS hasta llegar al destino. */
    private fun startSimulation(route: List<TrackingPointDto>) {
        if (route.size < 2) return
        simulation?.cancel()
        val dense = densify(route, stepsPerSegment = 10)
        simulation = viewModelScope.launch {
            dense.forEachIndexed { i, p ->
                _current.value = p
                _progress.value = i / (dense.size - 1).toFloat()
                _etaMinutes.value = ((dense.size - 1 - i) * STEP_MS / 60000.0).let {
                    kotlin.math.ceil(it).toInt()
                }
                if (i < dense.size - 1) delay(STEP_MS)
            }
        }
    }

    /** Interpola puntos intermedios entre cada par de la ruta para un movimiento fluido. */
    private fun densify(route: List<TrackingPointDto>, stepsPerSegment: Int): List<TrackingPointDto> =
        route.zipWithNext().flatMap { (a, b) ->
            (0 until stepsPerSegment).map { s ->
                val t = s / stepsPerSegment.toDouble()
                TrackingPointDto(a.lat + (b.lat - a.lat) * t, a.lng + (b.lng - a.lng) * t, a.time)
            }
        } + route.last()

    /** US20: confirma la llegada al destino final; la reserva pasa a "Finalizado". */
    fun confirmArrival(reservationId: String) {
        _completeState.value = UiState.Loading
        viewModelScope.launch {
            simulation?.cancel()
            _progress.value = 1f
            _etaMinutes.value = 0
            rentalRepo.completeTrip(reservationId)
                .onSuccess { _completeState.value = UiState.Success(Unit) }
                .onFailure { _completeState.value = UiState.Error(it.friendly()) }
        }
    }

    fun resetComplete() { _completeState.value = UiState.Idle }

    private companion object {
        const val STEP_MS = 1500L
    }
}
