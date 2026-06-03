package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.remote.StripeIntentService
import com.example.moveo_frontend.data.remote.api.BillingApi
import com.example.moveo_frontend.data.remote.dto.CreateIntentRequest
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import kotlinx.coroutines.delay

class BillingRepository(private val api: BillingApi) {
    /**
     * Métodos de pago guardados. El backend no expone /payment-methods, así que se sirve la lista
     * local (Yape, Plin, tarjeta). Yape va siempre primero para el pago rápido.
     */
    suspend fun methods(): Result<List<PaymentMethodDto>> = runCatching {
        delay(150)
        MockData.paymentMethods
    }

    /**
     * Crea un PaymentIntent y devuelve el client_secret para Stripe PaymentSheet.
     * - Si hay STRIPE_SECRET_KEY local: modo demo (el backend aún no tiene /payments/intent).
     * - Si no: se lo pide al backend (cuando esté implementado).
     */
    suspend fun createPaymentIntent(amountMinor: Long, reservationId: String? = null): Result<String> = runCatching {
        if (BuildConfig.STRIPE_SECRET_KEY.isNotBlank()) {
            return@runCatching StripeIntentService.createPaymentIntent(
                amountMinor = amountMinor,
                currency = BuildConfig.STRIPE_CURRENCY,
                secretKey = BuildConfig.STRIPE_SECRET_KEY
            )
        }
        api.createIntent(
            CreateIntentRequest(amountMinor, BuildConfig.STRIPE_CURRENCY, reservationId)
        ).clientSecret
    }
}