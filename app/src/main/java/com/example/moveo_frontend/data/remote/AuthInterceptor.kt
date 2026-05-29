package com.example.moveo_frontend.data.remote

import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val session: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { session.tokenBlocking() }
        val req = chain.request().newBuilder().apply {
            addHeader("Accept", "application/json")
            if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
        }.build()
        return chain.proceed(req)
    }
}
