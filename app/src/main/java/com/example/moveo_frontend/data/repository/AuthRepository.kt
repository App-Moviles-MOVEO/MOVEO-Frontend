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
            session.save("u1", MockData.currentUser.name, email, MockData.currentUser.role.name)
            return@runCatching MockData.currentUser.copy(email = email)
        }
        val user = api.login(LoginRequest(email, password))
        session.save(user.id.toString(), user.toDomain().name, user.email, user.role)
        user.toDomain()
    }

    suspend fun register(
        name: String, email: String, phone: String, password: String, role: String
    ): Result<User> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(600)
            session.save("u1", name, email, role)
            return@runCatching MockData.currentUser.copy(name = name, email = email)
        }
        // El backend pide firstName/lastName por separado y role "renter"/"owner".
        val trimmed = name.trim()
        val firstName = trimmed.substringBefore(' ', trimmed)
        val lastName = trimmed.substringAfter(' ', "")
        val backendRole = if (role.equals("PROVIDER", ignoreCase = true)) "owner" else "renter"
        val user = api.register(
            RegisterRequest(firstName, lastName, email, password, phone.ifBlank { null }, backendRole)
        )
        session.save(user.id.toString(), user.toDomain().name, user.email, user.role)
        user.toDomain()
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
        val userId = session.userIdBlocking()?.toIntOrNull()
            ?: error("No hay sesión activa")
        api.me(userId).toDomain()
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
