package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.AdventureRouteDto
import com.example.moveo_frontend.data.remote.dto.BookRequestDto
import com.example.moveo_frontend.data.remote.dto.BookSeatBody
import com.example.moveo_frontend.data.remote.dto.CreateCarpoolRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CarpoolingApi {
    @GET("adventure-routes")
    suspend fun list(
        @Query("type") type: String? = "carpool",
        @Query("onlyWomen") onlyWomen: Boolean? = null,
        @Query("community") community: String? = null
    ): List<AdventureRouteDto>

    @GET("adventure-routes/{id}")
    suspend fun detail(@Path("id") id: String): AdventureRouteDto

    @POST("adventure-routes")
    suspend fun publish(@Body req: CreateCarpoolRequest): AdventureRouteDto

    // Crea una solicitud PENDING; el asiento se descuenta cuando el conductor acepta.
    @POST("adventure-routes/{id}/book")
    suspend fun book(@Path("id") id: String, @Body req: BookSeatBody): BookRequestDto
}