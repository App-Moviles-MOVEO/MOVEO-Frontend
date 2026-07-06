package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.data.remote.dto.badgeLabel
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Estado KYC del usuario para el banner del perfil. */
data class KycInfo(val status: String, val rejectionReason: String?)

class ProfileViewModel : ViewModel() {
    private val auth = ServiceLocator.authRepo
    private val ops = ServiceLocator.operationsRepo
    private val rentals = ServiceLocator.rentalRepo

    private val _user = MutableStateFlow<UiState<User>>(UiState.Loading)
    val user = _user.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _kyc = MutableStateFlow<KycInfo?>(null)
    val kyc = _kyc.asStateFlow()

    init { load() }

    fun load() {
        _user.value = UiState.Loading
        viewModelScope.launch {
            auth.me()
                .onSuccess { me ->
                    val received = ops.myReviews().getOrDefault(emptyList())
                    _reviews.value = received

                    // Stats server-side (GET /users/{id}); si el deploy aún no las trae,
                    // se calculan en el cliente como fallback.
                    val detail = auth.myDetail().getOrNull()
                    val stats = detail?.stats

                    val clientAvg = if (received.isEmpty()) 0.0
                    else (received.map { it.rating }.average() * 10).roundToInt() / 10.0
                    val rating = stats?.reputation?.takeIf { it > 0 } ?: clientAvg

                    val clientTrips = rentals.myReservations().getOrDefault(emptyList())
                        .count { it.status == "Finalizado" }
                    val trips = stats?.completedRentals?.takeIf { it > 0 } ?: clientTrips

                    val kycStatus = detail?.kycStatus ?: "not_submitted"
                    _kyc.value = KycInfo(kycStatus, detail?.kycRejectionReason)

                    val badges = stats?.badges?.map(::badgeLabel)
                        ?: if (kycStatus == "approved") listOf("Verificado") else emptyList()

                    _user.value = UiState.Success(
                        me.copy(
                            rating = rating,
                            tripsCompleted = trips,
                            badges = badges,
                            verified = kycStatus == "approved"
                        )
                    )
                }
                .onFailure { _user.value = UiState.Error(it.friendly()) }
        }
    }
}
