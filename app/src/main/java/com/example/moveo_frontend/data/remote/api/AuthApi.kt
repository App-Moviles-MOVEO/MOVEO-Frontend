package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.AuthResponse
import com.example.moveo_frontend.data.remote.dto.ForgotPasswordRequest
import com.example.moveo_frontend.data.remote.dto.KycUploadResponse
import com.example.moveo_frontend.data.remote.dto.LoginRequest
import com.example.moveo_frontend.data.remote.dto.RegisterRequest
import com.example.moveo_frontend.data.remote.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest)

    @GET("auth/me")
    suspend fun me(): UserDto

    @Multipart
    @POST("auth/kyc")
    suspend fun uploadKyc(
        @Part dniFront: MultipartBody.Part,
        @Part dniBack: MultipartBody.Part,
        @Part selfie: MultipartBody.Part
    ): KycUploadResponse
}
