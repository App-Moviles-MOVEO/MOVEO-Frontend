package com.example.moveo_frontend.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * Crea un PaymentIntent llamando DIRECTAMENTE a la API de Stripe con la secret key.
 *
 * ⚠️ SOLO PARA DEMO / PRUEBAS SIN BACKEND.
 * En produccion el PaymentIntent debe crearlo tu backend (que es el unico lugar donde
 * debe vivir la secret key). Incrustar la secret key en la app permite que cualquiera la
 * extraiga del APK. Por eso se lee de local.properties y nunca se commitea.
 */
object StripeIntentService {
    private val client = OkHttpClient()

    /** Devuelve el client_secret del PaymentIntent creado. amountMinor = monto en centavos. */
    suspend fun createPaymentIntent(amountMinor: Long, currency: String, secretKey: String): String =
        withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("amount", amountMinor.toString())
                .add("currency", currency.lowercase())
                .add("automatic_payment_methods[enabled]", "true")
                .build()
            val request = Request.Builder()
                .url("https://api.stripe.com/v1/payment_intents")
                .addHeader("Authorization", "Bearer $secretKey")
                .post(body)
                .build()
            client.newCall(request).execute().use { resp ->
                val text = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    val msg = runCatching { JSONObject(text).getJSONObject("error").getString("message") }
                        .getOrNull() ?: "Error Stripe ${resp.code}"
                    error(msg)
                }
                JSONObject(text).getString("client_secret")
            }
        }
}