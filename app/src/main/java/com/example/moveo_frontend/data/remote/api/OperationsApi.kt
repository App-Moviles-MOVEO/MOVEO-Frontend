package com.example.moveo_frontend.data.remote.api

import com.example.moveo_frontend.data.remote.dto.ChatMessageDto
import com.example.moveo_frontend.data.remote.dto.NotificationDto
import com.example.moveo_frontend.data.remote.dto.ReviewDto
import com.example.moveo_frontend.data.remote.dto.SendMessageRequest
import com.example.moveo_frontend.data.remote.dto.SubmitReviewRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OperationsApi {
    @GET("reviews/me")
    suspend fun myReviews(): List<ReviewDto>

    @POST("reviews")
    suspend fun submitReview(@Body req: SubmitReviewRequest)

    @GET("notifications")
    suspend fun notifications(): List<NotificationDto>

    @GET("chats/{peerId}")
    suspend fun chat(@Path("peerId") peerId: String): List<ChatMessageDto>

    @POST("chats/send")
    suspend fun send(@Body req: SendMessageRequest): ChatMessageDto
}
