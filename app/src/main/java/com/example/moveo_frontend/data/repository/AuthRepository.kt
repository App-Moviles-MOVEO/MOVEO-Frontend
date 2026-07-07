package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.data.remote.api.AuthApi
import com.example.moveo_frontend.data.remote.dto.ChangePasswordRequest
import com.example.moveo_frontend.data.remote.dto.ForgotPasswordRequest
import com.example.moveo_frontend.data.remote.dto.LoginRequest
import com.example.moveo_frontend.data.remote.dto.RegisterRequest
import com.example.moveo_frontend.data.remote.dto.ResetPasswordRequest
import com.example.moveo_frontend.data.remote.dto.UserDetailDto
import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
        // Hidrata el género declarado (el backend lo persiste) para las rutas solo-mujeres.
        user.gender?.takeIf { it == "female" || it == "male" }?.let { session.setGender(it) }
        user.toDomain()
    }

    suspend fun register(
        name: String, email: String, phone: String, password: String, role: String,
        gender: String = ""
    ): Result<User> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(600)
            session.save("u1", name, email, role, kycCompleted = false)
            if (gender.isNotBlank()) session.setGender(gender)
            return@runCatching MockData.currentUser.copy(name = name, email = email)
        }
        // El backend pide firstName/lastName por separado y role "renter"/"owner".
        val trimmed = name.trim()
        val firstName = trimmed.substringBefore(' ', trimmed)
        val lastName = trimmed.substringAfter(' ', "")
        val backendRole = if (role.equals("PROVIDER", ignoreCase = true)) "owner" else "renter"
        val user = api.register(
            RegisterRequest(
                firstName, lastName, email, password, phone.ifBlank { null }, backendRole,
                gender = gender.ifBlank { null }
            )
        )
        session.save(user.id.toString(), user.toDomain().name, user.email, user.role, kycCompleted = false)
        if (gender.isNotBlank()) session.setGender(gender)
        user.toDomain()
    }

    // Devuelve el resetToken cuando el backend está en modo desarrollo (null en producción).
    suspend fun forgotPassword(email: String): Result<String?> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(400); return@runCatching null }
        api.forgotPassword(ForgotPasswordRequest(email)).resetToken
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(400); return@runCatching }
        api.resetPassword(ResetPasswordRequest(token, newPassword))
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

    /** Perfil extendido del usuario actual: stats server-side y estado/motivo KYC. */
    suspend fun myDetail(): Result<UserDetailDto> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) {
            delay(200)
            return@runCatching UserDetailDto(kycStatus = "approved")
        }
        val userId = session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")
        api.userDetail(userId)
    }

    suspend fun uploadKyc(dniFront: File, dniBack: File, selfie: File): Result<String> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(800); return@runCatching "approved" }
        val userId = session.userIdBlocking() ?: error("No hay sesión activa")
        val media = "image/jpeg".toMediaTypeOrNull()
        api.uploadKyc(
            userId.toRequestBody("text/plain".toMediaTypeOrNull()),
            MultipartBody.Part.createFormData("dniFront", dniFront.name, dniFront.asRequestBody(media)),
            MultipartBody.Part.createFormData("dniBack", dniBack.name, dniBack.asRequestBody(media)),
            MultipartBody.Part.createFormData("selfie", selfie.name, selfie.asRequestBody(media))
        ).status
    }

    /** Cambio de contraseña del usuario autenticado (auth/change-password). */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(400); return@runCatching }
        val userId = session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")
        api.changePassword(ChangePasswordRequest(userId, currentPassword, newPassword))
    }

    /**
     * US45: baja voluntaria y eliminación de datos. Es inmediata (sin aprobación de
     * un admin): borra la cuenta en el backend y limpia la sesión local.
     */
    suspend fun deleteAccount(): Result<Unit> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_AUTH)) { delay(400); session.clear(); return@runCatching }
        val userId = session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")
        api.deleteAccount(userId)
        session.clear()
    }

    suspend fun logout() = session.clear()
}
