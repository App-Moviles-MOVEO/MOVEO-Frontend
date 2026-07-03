package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ProfileViewModel : ViewModel() {
    private val auth = ServiceLocator.authRepo
    private val ops = ServiceLocator.operationsRepo
    private val rentals = ServiceLocator.rentalRepo

    private val _user = MutableStateFlow<UiState<User>>(UiState.Loading)
    val user = _user.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    init { load() }

    fun load() {
        _user.value = UiState.Loading
        viewModelScope.launch {
            auth.me()
                .onSuccess { me ->
                    // Rating real: promedio de las reseñas recibidas (0.0 = aún sin reseñas).
                    val received = ops.myReviews().getOrDefault(emptyList())
                    _reviews.value = received
                    val avg = if (received.isEmpty()) 0.0
                    else (received.map { it.rating }.average() * 10).roundToInt() / 10.0
                    // Viajes completados reales del usuario.
                    val trips = rentals.myReservations().getOrDefault(emptyList())
                        .count { it.status == "Finalizado" }
                    _user.value = UiState.Success(me.copy(rating = avg, tripsCompleted = trips))
                }
                .onFailure { _user.value = UiState.Error(it.friendly()) }
        }
    }
}
