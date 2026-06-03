package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.remote.api.BillingApi
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.data.remote.dto.PaymentRequest
import com.example.moveo_frontend.data.remote.dto.PaymentResponse
import kotlinx.coroutines.delay

class BillingRepository(private val api: BillingApi) {
    suspend fun methods(): Result<List<PaymentMethodDto>> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_BILLING)) {
            delay(200)
            return@runCatching MockData.paymentMethods
        }
        api.methods()
    }
    suspend fun pay(reservationId: String, method: String, amount: Int): Result<PaymentResponse> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_BILLING)) {
            delay(900)
            return@runCatching PaymentResponse(
                id = "pay_${System.currentTimeMillis()}",
                status = "success",
                receiptUrl = null
            )
        }
        api.pay(PaymentRequest(reservationId, method, amount))
    }
}
