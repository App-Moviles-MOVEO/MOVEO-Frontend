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

/**
 * US29: nivel de fidelidad derivado de la actividad y reputación reales del usuario.
 * El backend no expone un sistema de puntos, así que se calcula de forma
 * determinística: viajes completados y reseñas suman puntos, y una reputación
 * alta otorga un bono (recompensa a los usuarios mejor calificados).
 */
data class RewardStatus(
    val points: Int,
    val tier: String,
    val nextTier: String?,
    val progress: Float,
    val pointsToNext: Int
) {
    companion object {
        private val TIERS = listOf(0 to "Bronce", 500 to "Plata", 1500 to "Oro", 3000 to "Platino")

        fun from(tripsCompleted: Int, reviewsCount: Int, rating: Double): RewardStatus {
            val ratingBonus = if (rating >= 4.5) 100 else 0
            val points = tripsCompleted * 50 + reviewsCount * 10 + ratingBonus
            val currentIndex = TIERS.indexOfLast { points >= it.first }.coerceAtLeast(0)
            val (currentMin, tier) = TIERS[currentIndex]
            val next = TIERS.getOrNull(currentIndex + 1)
            return if (next == null) {
                RewardStatus(points, tier, null, 1f, 0)
            } else {
                val span = (next.first - currentMin).coerceAtLeast(1)
                val progress = ((points - currentMin).toFloat() / span).coerceIn(0f, 1f)
                RewardStatus(points, tier, next.second, progress, (next.first - points).coerceAtLeast(0))
            }
        }
    }
}

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

    private val _reward = MutableStateFlow<RewardStatus?>(null)
    val reward = _reward.asStateFlow()

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

                    // US29: puntos y nivel derivados de la actividad real.
                    _reward.value = RewardStatus.from(trips, received.size, rating)

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
