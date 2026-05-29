package com.example.moveo_frontend.data.remote

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.remote.api.AuthApi
import com.example.moveo_frontend.data.remote.api.BillingApi
import com.example.moveo_frontend.data.remote.api.CarpoolingApi
import com.example.moveo_frontend.data.remote.api.OperationsApi
import com.example.moveo_frontend.data.remote.api.RentalApi
import com.example.moveo_frontend.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile private var retrofit: Retrofit? = null

    private fun build(session: SessionManager): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun retrofit(session: SessionManager): Retrofit =
        retrofit ?: synchronized(this) { retrofit ?: build(session).also { retrofit = it } }

    fun authApi(session: SessionManager): AuthApi = retrofit(session).create(AuthApi::class.java)
    fun rentalApi(session: SessionManager): RentalApi = retrofit(session).create(RentalApi::class.java)
    fun carpoolingApi(session: SessionManager): CarpoolingApi = retrofit(session).create(CarpoolingApi::class.java)
    fun billingApi(session: SessionManager): BillingApi = retrofit(session).create(BillingApi::class.java)
    fun operationsApi(session: SessionManager): OperationsApi = retrofit(session).create(OperationsApi::class.java)
}
