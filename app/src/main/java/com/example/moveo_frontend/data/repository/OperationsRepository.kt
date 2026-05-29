package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.remote.api.OperationsApi
import com.example.moveo_frontend.data.remote.dto.ChatMessageDto
import com.example.moveo_frontend.data.remote.dto.NotificationDto
import com.example.moveo_frontend.data.remote.dto.SendMessageRequest
import com.example.moveo_frontend.data.remote.dto.SubmitReviewRequest
import kotlinx.coroutines.delay

class OperationsRepository(private val api: OperationsApi) {
    suspend fun myReviews(): Result<List<Review>> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(300)
            return@runCatching MockData.reviews
        }
        api.myReviews().map { it.toDomain() }
    }
    suspend fun submitReview(req: SubmitReviewRequest): Result<Unit> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) { delay(500); return@runCatching }
        api.submitReview(req)
    }
    suspend fun notifications(): Result<List<NotificationDto>> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(300)
            return@runCatching MockData.notifications
        }
        api.notifications()
    }
    suspend fun chat(peerId: String): Result<List<ChatMessageDto>> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(300)
            return@runCatching MockData.chatWith(peerId)
        }
        api.chat(peerId)
    }
    suspend fun send(to: String, body: String): Result<ChatMessageDto> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(200)
            return@runCatching ChatMessageDto(
                id = "c_${System.currentTimeMillis()}",
                from = "me",
                body = body,
                time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date()),
                mine = true
            )
        }
        api.send(SendMessageRequest(to, body))
    }
}
