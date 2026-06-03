package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.data.remote.api.AuthApi
import com.example.moveo_frontend.data.remote.dto.ForgotPasswordRequest
import com.example.moveo_frontend.data.remote.dto.LoginRequest
import com.example.moveo_frontend.data.remote.dto.RegisterRequest
import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AuthRepository(
    private val api: AuthApi,
    private val session: SessionManager
) {
    suspend fun login(email: String, password: String): Result<User> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(500)
            session.save("mock_token", "u1", MockData.currentUser.name, email, MockData.currentUser.role.name)
            return@runCatching MockData.currentUser.copy(email = email)
        }
        val res = api.login(LoginRequest(email, password))
        session.save(res.token, res.user.id, res.user.name, res.user.email, res.user.role)
        res.user.toDomain()
    }

    suspend fun register(
        name: String, email: String, phone: String, password: String, role: String
    ): Result<User> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(600)
            session.save("mock_token", "u1", name, email, role)
            return@runCatching MockData.currentUser.copy(name = name, email = email)
        }
        val res = api.register(RegisterRequest(name, email, phone, password, role))
        session.save(res.token, res.user.id, res.user.name, res.user.email, res.user.role)
        res.user.toDomain()
    }

    suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(400); return@runCatching }
        api.forgotPassword(ForgotPasswordRequest(email))
    }

    suspend fun me(): Result<User> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(300)
            return@runCatching MockData.currentUser
        }
        api.me().toDomain()
    }

    suspend fun uploadKyc(dniFront: File, dniBack: File, selfie: File): Result<String> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(800); return@runCatching "approved" }
        val media = "image/jpeg".toMediaTypeOrNull()
        api.uploadKyc(
            MultipartBody.Part.createFormData("dni_front", dniFront.name, dniFront.asRequestBody(media)),
            MultipartBody.Part.createFormData("dni_back", dniBack.name, dniBack.asRequestBody(media)),
            MultipartBody.Part.createFormData("selfie", selfie.name, selfie.asRequestBody(media))
        ).status
    }

    suspend fun logout() = session.clear()
}
