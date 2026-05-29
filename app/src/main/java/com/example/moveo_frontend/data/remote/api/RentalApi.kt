package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.CreateReservationRequest
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.data.remote.dto.ReservationDto
import com.example.moveo_frontend.data.remote.dto.VehicleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalApi {
    @GET("vehicles")
    suspend fun list(@Query("type") type: String? = null, @Query("q") q: String? = null): List<VehicleDto>

    @GET("vehicles/{id}")
    suspend fun detail(@Path("id") id: String): VehicleDto

    @POST("vehicles")
    suspend fun publish(@Body req: PublishVehicleRequest): VehicleDto

    @POST("reservations")
    suspend fun reserve(@Body req: CreateReservationRequest): ReservationDto

    @GET("reservations")
    suspend fun myReservations(): List<ReservationDto>

    @GET("reservations/{id}")
    suspend fun reservation(@Path("id") id: String): ReservationDto
}
