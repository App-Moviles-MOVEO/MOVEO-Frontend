package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.AdventureRouteDto
import com.example.moveo_frontend.data.remote.dto.CreateSupportTicketRequest
import com.example.moveo_frontend.data.remote.dto.CreateUserReviewRequest
import com.example.moveo_frontend.data.remote.dto.CreateVehicleReviewRequest
import com.example.moveo_frontend.data.remote.dto.MessageResourceDto
import com.example.moveo_frontend.data.remote.dto.NotificationResourceDto
import com.example.moveo_frontend.data.remote.dto.PatchRentalRequest
import com.example.moveo_frontend.data.remote.dto.RentalDto
import com.example.moveo_frontend.data.remote.dto.SendMessageRequest
import com.example.moveo_frontend.data.remote.dto.UserReviewResourceDto
import com.example.moveo_frontend.data.remote.dto.VehicleReviewResourceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OperationsApi {
    // ===== Reseñas =====
    /** Reseñas entre usuarios recibidas por el usuario (perfil). */
    @GET("user-reviews")
    suspend fun userReviewsReceived(@Query("reviewedUserId") reviewedUserId: Int): List<UserReviewResourceDto>

    /** Reseñas de alquiler recibidas por el usuario (perfil). */
    @GET("reviews/reviewee/{revieweeId}")
    suspend fun rentalReviewsReceived(@Path("revieweeId") revieweeId: Int): List<VehicleReviewResourceDto>

    /** Reseña de un alquiler (alimenta el rating del vehículo). */
    @POST("reviews")
    suspend fun submitRentalReview(@Body req: CreateVehicleReviewRequest): VehicleReviewResourceDto

    /** Reseña entre usuarios (carpool u otros flujos sin rental). */
    @POST("user-reviews")
    suspend fun submitUserReview(@Body req: CreateUserReviewRequest): UserReviewResourceDto

    // ===== Notificaciones =====
    @GET("notifications/user/{userId}")
    suspend fun notifications(@Path("userId") userId: Int): List<NotificationResourceDto>

    // ===== Chat 1 a 1 =====
    @GET("messages")
    suspend fun conversation(
        @Query("userId") userId: Int,
        @Query("otherUserId") otherUserId: Int
    ): List<MessageResourceDto>

    @POST("messages")
    suspend fun send(@Body req: SendMessageRequest): MessageResourceDto

    @PUT("messages/read")
    suspend fun markConversationRead(
        @Query("userId") userId: Int,
        @Query("otherUserId") otherUserId: Int
    )

    // ===== Lookups para resolver a quién se califica =====
    @GET("rentals/{id}")
    suspend fun rental(@Path("id") id: String): RentalDto

    /** Marca la reserva como calificada (vehicleRated/vehicleRating) tras enviar la reseña. */
    @PATCH("rentals/{id}")
    suspend fun patchRental(@Path("id") id: String, @Body req: PatchRentalRequest): RentalDto

    @GET("adventure-routes/{id}")
    suspend fun adventureRoute(@Path("id") id: String): AdventureRouteDto

    // ===== Soporte / Emergencia =====
    /** US08: crea un ticket de soporte (se usa para la alerta de emergencia). */
    @POST("support-tickets")
    suspend fun createSupportTicket(@Body req: CreateSupportTicketRequest)
}