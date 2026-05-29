package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.BookSeatRequest
import com.example.moveo_frontend.data.remote.dto.CarpoolRouteDto
import com.example.moveo_frontend.data.remote.dto.PublishRouteRequest
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CarpoolingApi {
    @GET("routes")
    suspend fun list(
        @Query("onlyWomen") onlyWomen: Boolean? = null,
        @Query("verified") verified: Boolean? = null
    ): List<CarpoolRouteDto>

    @GET("routes/{id}")
    suspend fun detail(@Path("id") id: String): CarpoolRouteDto

    @POST("routes")
    suspend fun publish(@Body req: PublishRouteRequest): CarpoolRouteDto

    @POST("routes/book")
    suspend fun book(@Body req: BookSeatRequest)

    @GET("routes/{id}/tracking")
    suspend fun tracking(@Path("id") id: String): List<TrackingPointDto>
}
