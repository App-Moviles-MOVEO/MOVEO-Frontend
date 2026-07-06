package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.CreatePaymentRequest
import com.example.moveo_frontend.data.remote.dto.CreateRentalRequest
import com.example.moveo_frontend.data.remote.dto.CreatedPaymentDto
import com.example.moveo_frontend.data.remote.dto.InvoiceDto
import com.example.moveo_frontend.data.remote.dto.PatchRentalRequest
import com.example.moveo_frontend.data.remote.dto.PaymentRecordDto
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.data.remote.dto.RefundRequest
import com.example.moveo_frontend.data.remote.dto.RefundResponse
import com.example.moveo_frontend.data.remote.dto.RentalDto
import com.example.moveo_frontend.data.remote.dto.RentalPayRequest
import com.example.moveo_frontend.data.remote.dto.RentalPayResponse
import com.example.moveo_frontend.data.remote.dto.VehicleDto
import com.example.moveo_frontend.data.remote.dto.VehicleReviewResourceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalApi {
    @GET("vehicles")
    suspend fun list(
        @Query("bodyType") bodyType: String? = null,
        @Query("district") district: String? = null
    ): List<VehicleDto>

    @GET("vehicles/{id}")
    suspend fun detail(@Path("id") id: String): VehicleDto

    @POST("vehicles")
    suspend fun publish(@Body req: PublishVehicleRequest): VehicleDto

    @POST("rentals")
    suspend fun createRental(@Body req: CreateRentalRequest): RentalDto

    // Pago en un paso: crea el pago, lo enlaza a la reserva y la marca pagada.
    @POST("rentals/{id}/pay")
    suspend fun payRental(@Path("id") id: String, @Body req: RentalPayRequest): RentalPayResponse

    @GET("rentals/user/{userId}")
    suspend fun userRentals(@Path("userId") userId: Int): List<RentalDto>

    @GET("rentals/{id}")
    suspend fun rental(@Path("id") id: String): RentalDto

    // Reservas (todas o de un vehículo). Se usa para calcular disponibilidad por fechas
    // mientras el backend no expone /vehicles/{id}/availability (ver BACKEND_REQUESTS.md).
    @GET("rentals")
    suspend fun rentals(@Query("vehicleId") vehicleId: Int? = null): List<RentalDto>

    // Cambio de estado de la reserva: cancelar (US54) o completar al llegar (US20).
    @PATCH("rentals/{id}")
    suspend fun patchRental(@Path("id") id: String, @Body req: PatchRentalRequest): RentalDto

    // Registro del reembolso automático al cancelar (US26/US33).
    @POST("payments")
    suspend fun createPayment(@Body req: CreatePaymentRequest): CreatedPaymentDto

    // Pagos de una reserva: para reembolsar solo lo efectivamente pagado.
    @GET("payments/rental/{rentalId}")
    suspend fun rentalPayments(@Path("rentalId") rentalId: Int): List<PaymentRecordDto>

    // Reembolso server-side con política incluida (US26/US33).
    @POST("payments/{id}/refund")
    suspend fun refundPayment(@Path("id") paymentId: Int, @Body req: RefundRequest): RefundResponse

    // Comprobante oficial del alquiler (US25). 422 si aún no hay pago completado.
    @GET("rentals/{id}/invoice")
    suspend fun invoice(@Path("id") id: String): InvoiceDto

    // Reseñas de un vehículo, para mostrarlas en su detalle antes de reservar.
    @GET("reviews")
    suspend fun vehicleReviews(@Query("vehicleId") vehicleId: Int): List<VehicleReviewResourceDto>
}