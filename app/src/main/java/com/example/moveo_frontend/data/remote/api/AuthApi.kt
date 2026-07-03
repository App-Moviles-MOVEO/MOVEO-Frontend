package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.ForgotPasswordRequest
import com.example.moveo_frontend.data.remote.dto.ForgotPasswordResponse
import com.example.moveo_frontend.data.remote.dto.KycUploadResponse
import com.example.moveo_frontend.data.remote.dto.LoginRequest
import com.example.moveo_frontend.data.remote.dto.RegisterRequest
import com.example.moveo_frontend.data.remote.dto.ResetPasswordRequest
import com.example.moveo_frontend.data.remote.dto.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AuthApi {
    // El backend devuelve el usuario directo (sin token). 401 si las credenciales fallan.
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): UserDto

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): UserDto

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): ForgotPasswordResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body req: ResetPasswordRequest)

    @GET("auth/me")
    suspend fun me(@Query("userId") userId: Int): UserDto

    // El backend espera userId + al menos un documento. Nombres de campo: dniFront, dniBack, selfie.
    @Multipart
    @POST("auth/kyc")
    suspend fun uploadKyc(
        @Part("userId") userId: RequestBody,
        @Part dniFront: MultipartBody.Part,
        @Part dniBack: MultipartBody.Part,
        @Part selfie: MultipartBody.Part
    ): KycUploadResponse
}
