package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.remote.api.OperationsApi
import com.example.moveo_frontend.data.remote.dto.ChatMessageDto
import com.example.moveo_frontend.data.remote.dto.CreateUserReviewRequest
import com.example.moveo_frontend.data.remote.dto.CreateVehicleReviewRequest
import com.example.moveo_frontend.data.remote.dto.NotificationDto
import com.example.moveo_frontend.data.remote.dto.PatchRentalRequest
import com.example.moveo_frontend.data.remote.dto.SendMessageRequest
import com.example.moveo_frontend.data.remote.dto.SubmitReviewRequest
import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.delay

class OperationsRepository(
    private val api: OperationsApi,
    private val session: SessionManager
) {
    private fun mock() = BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_OPERATIONS

    private suspend fun currentUserId(): Int =
        session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")

    /** Reseñas recibidas por el usuario: entre usuarios (/user-reviews) + de alquiler (/reviews). */
    suspend fun myReviews(): Result<List<Review>> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.reviews
        }
        val me = currentUserId()
        val userReviews = runCatching { api.userReviewsReceived(me) }.getOrDefault(emptyList())
        val rentalReviews = runCatching { api.rentalReviewsReceived(me) }.getOrDefault(emptyList())
        (userReviews.map { it.createdAt to it.toDomain() } + rentalReviews.map { it.createdAt to it.toDomain() })
            .sortedByDescending { it.first ?: "" }
            .map { it.second }
    }

    /**
     * Envía una calificación. La UI solo conoce el id de la reserva o de la ruta;
     * aquí se resuelve el destinatario real: si hay un rental, POST /Reviews contra su owner;
     * si es una ruta carpool, POST /user-reviews contra el conductor (ownerId de la ruta).
     */
    suspend fun submitReview(req: SubmitReviewRequest): Result<Unit> = runCatching {
        if (mock()) { delay(500); return@runCatching }
        val me = currentUserId()
        val refId = req.reservationId ?: req.routeId

        val rental = refId?.let { runCatching { api.rental(it) }.getOrNull() }
        if (rental != null) {
            api.submitRentalReview(
                CreateVehicleReviewRequest(
                    rentalId = rental.id,
                    vehicleId = rental.vehicleId,
                    reviewerId = me,
                    revieweeId = rental.ownerId,
                    rating = req.rating,
                    comment = req.comment
                )
            )
            // Deja constancia en la reserva (vehicleRated/vehicleRating) para que la UI
            // muestre la nota y no permita calificar dos veces. Best-effort.
            runCatching {
                api.patchRental(
                    rental.id.toString(),
                    PatchRentalRequest(vehicleRated = true, vehicleRating = req.rating)
                )
            }
            return@runCatching
        }

        val route = refId?.let { runCatching { api.adventureRoute(it) }.getOrNull() }
        val reviewedUserId = route?.ownerId
            ?: req.targetUserId.toIntOrNull()
            ?: error("No se pudo identificar a quién calificar")
        api.submitUserReview(
            CreateUserReviewRequest(
                reviewerId = me,
                reviewedUserId = reviewedUserId,
                rating = req.rating,
                comment = req.comment
            )
        )
        Unit
    }

    suspend fun notifications(): Result<List<NotificationDto>> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.notifications
        }
        api.notifications(currentUserId()).map { it.toUi() }
    }

    /** Conversación con otro usuario; de paso la marca como leída en el backend. */
    suspend fun chat(peerId: String): Result<List<ChatMessageDto>> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.chatWith(peerId)
        }
        val me = currentUserId()
        val other = peerId.toIntOrNull() ?: error("Conversación inválida")
        val messages = api.conversation(me, other).map { it.toUi(me) }
        runCatching { api.markConversationRead(me, other) } // best-effort, no rompe la carga
        messages
    }

    suspend fun send(to: String, body: String): Result<ChatMessageDto> = runCatching {
        if (mock()) {
            delay(200)
            return@runCatching ChatMessageDto(
                id = "c_${System.currentTimeMillis()}",
                from = "me",
                body = body,
                time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date()),
                mine = true
            )
        }
        val me = currentUserId()
        val other = to.toIntOrNull() ?: error("Conversación inválida")
        api.send(SendMessageRequest(senderId = me, receiverId = other, content = body)).toUi(me)
    }
}