package com.example.moveo_frontend.data.remote.dto

import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.data.UserRole
import com.example.moveo_frontend.data.Vehicle

// ===== Helpers de presentacion (backend -> etiquetas en español de la app) =====
internal fun bodyTypeDisplay(v: String?): String = when (v?.lowercase()) {
    "compact" -> "Compacto"
    "sedan" -> "Sedán"
    "suv" -> "SUV"
    "pickup" -> "Pickup"
    null, "" -> "Auto"
    else -> v.replaceFirstChar { it.uppercase() }
}

/** Inverso de [bodyTypeDisplay]: etiqueta de la app -> valor que filtra el backend (?bodyType=). */
internal fun bodyTypeQuery(display: String?): String? = when (display?.lowercase()) {
    "compacto" -> "compact"
    "sedán", "sedan" -> "sedan"
    "suv" -> "suv"
    "pickup" -> "pickup"
    else -> null
}

internal fun transmissionDisplay(v: String?): String = when (v?.lowercase()) {
    "automatic" -> "Automático"
    "manual" -> "Mecánico"
    null, "" -> "—"
    else -> v.replaceFirstChar { it.uppercase() }
}

internal fun fuelDisplay(v: String?): String = when (v?.lowercase()) {
    "gasoline" -> "Gasolina"
    "diesel" -> "Diésel"
    "electric" -> "Eléctrico"
    "hybrid" -> "Híbrido"
    "gas", "glp" -> "GLP"
    null, "" -> "—"
    else -> v.replaceFirstChar { it.uppercase() }
}

internal fun rentalStatusDisplay(v: String?): String = when (v?.lowercase()) {
    "pending" -> "Pendiente"
    "accepted" -> "Aceptado"
    "active" -> "En curso"
    "completed" -> "Finalizado"
    "cancelled", "canceled" -> "Cancelado"
    null, "" -> "—"
    else -> v.replaceFirstChar { it.uppercase() }
}

/** "2026-06-10T09:00:00Z" -> "10 jun". Tolerante a formatos sin zona. */
internal fun shortDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    val datePart = iso.take(10) // yyyy-MM-dd
    return try {
        val p = datePart.split("-")
        val months = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
        "${p[2].toInt()} ${months[p[1].toInt() - 1]}"
    } catch (_: Exception) { datePart }
}

// ===== AUTH / IAM =====
// El backend MOVEO es stateless: login/register devuelven el objeto usuario directo (sin token).
// La "sesion" es el id del usuario, que se manda como ?userId= donde haga falta.
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val role: String = "renter"
)
data class ForgotPasswordRequest(val email: String)
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = "renter",
    val phone: String? = null,
    val avatar: String? = null
) {
    fun toDomain() = User(
        name = listOf(firstName, lastName).filter { it.isNotBlank() }
            .joinToString(" ").ifBlank { email },
        email = email,
        role = when (role.lowercase()) {
            "owner" -> UserRole.PROVIDER
            "renter" -> UserRole.RENTER
            else -> UserRole.PASSENGER
        }
    )
}

data class KycUploadResponse(val status: String)

// ===== RENTAL (backend /vehicles, /rentals) =====
// Mapea VehicleResource del backend (location anidada, dailyPrice decimal, bodyType) al dominio plano de la app.
data class VehicleLocationDto(
    val district: String = "",
    val address: String = "",
    val lat: Double? = null,
    val lng: Double? = null
)

data class VehicleDto(
    val id: Int = 0,
    val ownerId: Int = 0,
    val brand: String = "",
    val model: String = "",
    val year: Int = 0,
    val color: String = "",
    val transmission: String = "",
    val fuelType: String = "",
    val seats: Int = 0,
    val licensePlate: String = "",
    val location: VehicleLocationDto = VehicleLocationDto(),
    val dailyPrice: Double = 0.0,
    val depositAmount: Double? = null,
    val status: String = "active",
    val description: String? = null,
    val images: List<String> = emptyList(),
    val features: List<String> = emptyList(),
    val bodyType: String? = null,
    val ownerName: String? = null,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0
) {
    fun toDomain() = Vehicle(
        id = id.toString(),
        brand = brand, model = model, year = year,
        pricePerDay = dailyPrice.toInt(),
        rating = rating,
        ownerName = ownerName ?: "",
        ownerVerified = true,
        location = listOf(location.district, location.address).filter { it.isNotBlank() }.joinToString(", "),
        type = bodyTypeDisplay(bodyType),
        transmission = transmissionDisplay(transmission),
        seats = seats,
        fuel = fuelDisplay(fuelType),
        description = description ?: "",
        imageEmoji = "🚗",
        ownerId = ownerId,
        imageUrl = images.firstOrNull()
    )
}

// Crear reserva en el backend (CreateRentalResource). Fechas en ISO 8601 UTC.
data class CreateRentalRequest(
    val vehicleId: Int,
    val renterId: Int,
    val ownerId: Int,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val pickupLocation: String? = null,
    val returnLocation: String? = null
)

// Pago en un paso: POST /rentals/{id}/pay. paymentMethod "yape" por defecto.
data class RentalPayRequest(
    val paymentMethod: String = "yape",
    val amount: Double? = null,
    val currency: String = "PEN",
    val type: String = "rental_payment"
)
data class RentalPayResponse(
    val rental: RentalDto? = null,
    val payment: PaymentInfoDto? = null
)
data class PaymentInfoDto(
    val id: Int = 0,
    val status: String = "",
    val method: String = "",
    val amount: Double = 0.0
)

data class RentalDto(
    val id: Int = 0,
    val vehicleId: Int = 0,
    val renterId: Int = 0,
    val ownerId: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val totalPrice: Double = 0.0,
    val status: String = "pending",
    val vehicleName: String? = null,
    val vehicleImage: String? = null
) {
    fun toDomain() = Reservation(
        id = id.toString(),
        vehicleName = vehicleName ?: "Vehículo",
        startDate = shortDate(startDate),
        endDate = shortDate(endDate),
        total = totalPrice.toInt(),
        status = rentalStatusDisplay(status)
    )
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

// ===== CARPOOLING (mapeado sobre backend /adventure-routes con type="carpool") =====
// El backend no devuelve el nombre del conductor en la ruta; se usa un fallback.
data class AdventureRouteDto(
    val id: Int = 0,
    val ownerId: Int = 0,
    val title: String = "",
    val startLocation: String = "",
    val endLocation: String = "",
    val type: String = "carpool",
    val vehicleName: String? = null,
    val departureDate: String? = null,
    val departureTime: String? = null,
    val seatsTotal: Int = 0,
    val seatsAvailable: Int = 0,
    val pricePerSeat: Double = 0.0,
    val onlyWomen: Boolean = false,
    val community: String? = null,
    val rating: Double = 0.0,
    val lat: Double? = null,
    val lng: Double? = null,
    val driverName: String? = null
) {
    fun toDomain() = CarpoolRoute(
        id = id.toString(),
        driverName = driverName ?: community?.let { "Conductor ($it)" } ?: "Conductor MOVEO",
        driverRating = rating,
        verified = true,
        origin = startLocation,
        destination = endLocation,
        departureTime = departureTime ?: "—",
        date = shortDate(departureDate),
        seatsAvailable = seatsAvailable,
        pricePerSeat = pricePerSeat.toInt(),
        vehicleModel = vehicleName ?: "Vehículo",
        community = community ?: "",
        onlyWomen = onlyWomen
    )
}

// Cuerpo para POST /adventure-routes (viaje carpool).
data class CreateCarpoolRequest(
    val ownerId: Int,
    val name: String,
    val title: String,
    val description: String,
    val startLocation: String,
    val endLocation: String,
    val type: String = "carpool",
    val duration: Int = 1,
    val difficulty: String = "easy",
    val estimatedCost: Double = 0.0,
    val departureDate: String,
    val departureTime: String,
    val seatsTotal: Int,
    val seatsAvailable: Int,
    val pricePerSeat: Double,
    val onlyWomen: Boolean = false,
    val community: String = "",
    val vehicleName: String = "Mi vehículo"
)

// Request que la UI manda al repositorio (se traduce a CreateCarpoolRequest con el ownerId de sesión).
data class PublishRouteRequest(
    val origin: String,
    val destination: String,
    val date: String,
    val time: String,
    val seats: Int,
    val price: Int,
    val recurring: Boolean
)

data class BookSeatBody(val seats: Int)

// ===== BILLING =====
data class PaymentResponse(
    val id: String,
    val status: String,
    val receiptUrl: String? = null
)
data class PaymentMethodDto(val id: String, val type: String, val display: String)

// Stripe: el backend crea el PaymentIntent y devuelve el client_secret.
data class CreateIntentRequest(
    val amount: Long,      // monto en centavos
    val currency: String,
    val reservationId: String? = null
)
data class CreateIntentResponse(val clientSecret: String)

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
