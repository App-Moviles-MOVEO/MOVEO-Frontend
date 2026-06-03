package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.CreateRentalRequest
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.data.remote.dto.RentalDto
import com.example.moveo_frontend.data.remote.dto.RentalPayRequest
import com.example.moveo_frontend.data.remote.dto.RentalPayResponse
import com.example.moveo_frontend.data.remote.dto.VehicleDto
import retrofit2.http.Body
import retrofit2.http.GET
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
}