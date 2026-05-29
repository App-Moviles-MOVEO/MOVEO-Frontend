package com.example.moveo_frontend.data.remote.dto

import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.data.UserRole
import com.example.moveo_frontend.data.Vehicle

// ===== AUTH / IAM =====
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)
data class ForgotPasswordRequest(val email: String)
data class AuthResponse(
    val token: String,
    val user: UserDto
)
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val rating: Double = 0.0,
    val tripsCompleted: Int = 0,
    val verified: Boolean = false,
    val badges: List<String> = emptyList(),
    val rewardPoints: Int = 0
) {
    fun toDomain() = User(
        name = name,
        email = email,
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.PASSENGER),
        rating = rating,
        tripsCompleted = tripsCompleted,
        verified = verified,
        badges = badges,
        rewardPoints = rewardPoints
    )
}

data class KycUploadResponse(val status: String)

// ===== RENTAL =====
data class VehicleDto(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Int,
    val rating: Double,
    val ownerName: String,
    val ownerVerified: Boolean,
    val location: String,
    val type: String,
    val transmission: String,
    val seats: Int,
    val fuel: String,
    val description: String,
    val imageUrl: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
) {
    fun toDomain() = Vehicle(
        id = id, brand = brand, model = model, year = year,
        pricePerDay = pricePerDay, rating = rating, ownerName = ownerName,
        ownerVerified = ownerVerified, location = location, type = type,
        transmission = transmission, seats = seats, fuel = fuel,
        description = description, imageEmoji = "🚗"
    )
}

data class CreateReservationRequest(
    val vehicleId: String,
    val startDate: String,
    val endDate: String
)

data class ReservationDto(
    val id: String,
    val vehicleName: String,
    val startDate: String,
    val endDate: String,
    val total: Int,
    val status: String
) {
    fun toDomain() = Reservation(id, vehicleName, startDate, endDate, total, status)
}

data class PublishVehicleRequest(
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Int,
    val location: String,
    val type: String,
    val transmission: String,
    val seats: Int,
    val fuel: String,
    val description: String
)

// ===== CARPOOLING =====
data class CarpoolRouteDto(
    val id: String,
    val driverName: String,
    val driverRating: Double,
    val verified: Boolean,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val date: String,
    val seatsAvailable: Int,
    val pricePerSeat: Int,
    val vehicleModel: String,
    val community: String,
    val onlyWomen: Boolean = false,
    val originLat: Double? = null,
    val originLng: Double? = null,
    val destLat: Double? = null,
    val destLng: Double? = null
) {
    fun toDomain() = CarpoolRoute(
        id = id, driverName = driverName, driverRating = driverRating,
        verified = verified, origin = origin, destination = destination,
        departureTime = departureTime, date = date,
        seatsAvailable = seatsAvailable, pricePerSeat = pricePerSeat,
        vehicleModel = vehicleModel, community = community, onlyWomen = onlyWomen
    )
}

data class PublishRouteRequest(
    val origin: String,
    val destination: String,
    val date: String,
    val time: String,
    val seats: Int,
    val price: Int,
    val recurring: Boolean
)

data class BookSeatRequest(val routeId: String, val seats: Int)

// ===== BILLING =====
data class PaymentRequest(
    val reservationId: String,
    val method: String,
    val amount: Int
)
data class PaymentResponse(
    val id: String,
    val status: String,
    val receiptUrl: String? = null
)
data class PaymentMethodDto(val id: String, val type: String, val display: String)

// ===== OPERATIONS =====
data class ReviewDto(
    val author: String,
    val rating: Int,
    val comment: String,
    val date: String
) {
    fun toDomain() = Review(author, rating, comment, date)
}

data class SubmitReviewRequest(
    val targetUserId: String,
    val reservationId: String?,
    val routeId: String?,
    val rating: Int,
    val comment: String
)

data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val read: Boolean
)

data class ChatMessageDto(
    val id: String,
    val from: String,
    val body: String,
    val time: String,
    val mine: Boolean
)

data class SendMessageRequest(val to: String, val body: String)

data class TrackingPointDto(val lat: Double, val lng: Double, val time: String)
