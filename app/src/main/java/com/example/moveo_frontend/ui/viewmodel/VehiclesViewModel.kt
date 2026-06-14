package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.data.session.RentalDateStore
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class VehiclesViewModel : ViewModel() {
    private val repo = ServiceLocator.rentalRepo

    // Chips del catálogo. Compacto/Sedán/SUV filtran en el backend (bodyType);
    // Automático/Eléctrico se filtran en la app (el backend aún no los soporta, ver BACKEND_REQUESTS.md P4).
    val filters = listOf("Todos", "Compacto", "Sedán", "SUV", "Automático", "Eléctrico")
    private val bodyTypeFilters = setOf("Compacto", "Sedán", "SUV")

    private val _state = MutableStateFlow<UiState<List<Vehicle>>>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _filter = MutableStateFlow("Todos")
    val filter = _filter.asStateFlow()

    private val _district = MutableStateFlow("")
    val district = _district.asStateFlow()

    // Fechas compartidas con detalle y pago.
    val startMillis = RentalDateStore.startMillis
    val endMillis = RentalDateStore.endMillis

    /** Ubicación del usuario (lat, lng). Si está presente, se muestran distancias y se ordena por cercanía. */
    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation = _userLocation.asStateFlow()

    private var searchJob: Job? = null

    init { load() }

    fun setFilter(f: String) {
        _filter.value = f
        load()
    }

    fun setDistrict(text: String) {
        _district.value = text
        // Debounce para no pegarle al backend en cada tecla.
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            load()
        }
    }

    fun setDates(start: Long, end: Long) {
        RentalDateStore.set(start, end)
        load()
    }

    fun clearDates() {
        RentalDateStore.clear()
        load()
    }

    fun setUserLocation(lat: Double, lng: Double) {
        _userLocation.value = lat to lng
        load()
    }

    /** Distancia en km del usuario al vehículo, o null si falta alguna coordenada. */
    fun distanceKm(v: Vehicle): Double? {
        val (uLat, uLng) = _userLocation.value ?: return null
        val vLat = v.lat ?: return null
        val vLng = v.lng ?: return null
        return haversineKm(uLat, uLng, vLat, vLng)
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val f = _filter.value
            val type = f.takeIf { it in bodyTypeFilters }
            repo.vehicles(type = type, district = _district.value.trim().ifBlank { null })
                .onSuccess { list ->
                    var out = when (f) {
                        "Automático" -> list.filter { it.transmission.equals("Automático", ignoreCase = true) }
                        "Eléctrico" -> list.filter { it.fuel.equals("Eléctrico", ignoreCase = true) }
                        else -> list
                    }
                    out = filterByDates(out)
                    _userLocation.value?.let { (uLat, uLng) ->
                        out = out.sortedBy { v ->
                            if (v.lat != null && v.lng != null) haversineKm(uLat, uLng, v.lat, v.lng)
                            else Double.MAX_VALUE
                        }
                    }
                    _state.value = UiState.Success(out)
                }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }

    /** Si hay fechas elegidas, excluye vehículos con reservas que se solapen. */
    private suspend fun filterByDates(vehicles: List<Vehicle>): List<Vehicle> {
        val start = RentalDateStore.startMillis.value ?: return vehicles
        val end = RentalDateStore.endMillis.value ?: return vehicles
        // Si la consulta de reservas falla, no se rompe el catálogo: se muestra sin filtrar.
        val busyByVehicle = repo.allBusyRanges().getOrDefault(emptyMap())
        return vehicles.filter { v ->
            val busy = busyByVehicle[v.id.toIntOrNull()] ?: return@filter true
            busy.none { it.overlaps(start, end) }
        }
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}