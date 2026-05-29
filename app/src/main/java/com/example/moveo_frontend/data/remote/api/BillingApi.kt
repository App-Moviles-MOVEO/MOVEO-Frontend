package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.data.remote.dto.PaymentRequest
import com.example.moveo_frontend.data.remote.dto.PaymentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BillingApi {
    @GET("payment-methods")
    suspend fun methods(): List<PaymentMethodDto>

    @POST("payments")
    suspend fun pay(@Body req: PaymentRequest): PaymentResponse
}
