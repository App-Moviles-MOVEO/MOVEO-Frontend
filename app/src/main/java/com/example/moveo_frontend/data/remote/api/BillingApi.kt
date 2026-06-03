package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.CreateIntentRequest
import com.example.moveo_frontend.data.remote.dto.CreateIntentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BillingApi {
    // Opcional: si el backend implementa el PaymentIntent de Stripe, devuelve el client_secret.
    // Por ahora el flujo real usa Yape vía /rentals/{id}/pay; Stripe corre en modo demo local.
    @POST("payments/intent")
    suspend fun createIntent(@Body req: CreateIntentRequest): CreateIntentResponse
}