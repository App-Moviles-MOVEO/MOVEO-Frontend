package com.example.moveo_frontend.data

data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Int,
    val rating: Double,
    val ownerName: String,
    val ownerVerified: Boolean,
    val location: String,
    val type: String, // Compacto, Sedán, SUV
    val transmission: String,
    val seats: Int,
    val fuel: String,
    val description: String,
    val imageEmoji: String = "🚗",
    val ownerId: Int = 0,
    val imageUrl: String? = null
)

data class CarpoolRoute(
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
    val community: String, // ej. UPC, San Isidro
    val onlyWomen: Boolean = false
)

data class User(
    val name: String,
    val email: String,
    val role: UserRole,
    val rating: Double = 4.9,
    val tripsCompleted: Int = 47,
    val verified: Boolean = true,
    val badges: List<String> = listOf("KYC", "UPC", "Plata"),
    val rewardPoints: Int = 450
)

enum class UserRole { PROVIDER, RENTER, PASSENGER }

data class Review(
    val author: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class Reservation(
    val id: String,
    val vehicleName: String,
    val startDate: String,
    val endDate: String,
    val total: Int,
    val status: String // Confirmado, En curso, Finalizado
)

/** Asiento reservado en un viaje compartido (carpool). Aparece en "Mis reservas". */
data class CarpoolBooking(
    val routeId: String,
    val origin: String,
    val destination: String,
    val date: String,
    val departureTime: String,
    val driverName: String,
    val seats: Int,
    val total: Int
)
