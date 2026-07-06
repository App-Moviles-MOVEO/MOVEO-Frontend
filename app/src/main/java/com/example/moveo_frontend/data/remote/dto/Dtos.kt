package com.example.moveo_frontend.data.remote.dto

import com.example.moveo_frontend.data.BusyRange
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

/** Parsea un createdAt ISO del backend (UTC, con o sin 'Z'/fracción) a epoch millis. */
internal fun parseIsoUtc(iso: String?): Long? {
    if (iso.isNullOrBlank() || iso.length < 19) return null
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.parse(iso.take(19))?.time
    } catch (_: Exception) { null }
}

/** "2026-06-12T09:55:00" -> "hace 5 min" / "hace 2 h" / "ayer" / "10 jun". */
internal fun relativeTime(iso: String?): String {
    val then = parseIsoUtc(iso) ?: return "—"
    val mins = (System.currentTimeMillis() - then) / 60000
    return when {
        mins < 1 -> "ahora"
        mins < 60 -> "hace $mins min"
        mins < 24 * 60 -> "hace ${mins / 60} h"
        mins < 48 * 60 -> "ayer"
        mins < 7 * 24 * 60 -> "hace ${mins / (24 * 60)} d"
        else -> shortDate(iso)
    }
}

/** Hora local "HH:mm" a partir del createdAt UTC del backend (para burbujas de chat). */
internal fun localHourMinute(iso: String?): String {
    val millis = parseIsoUtc(iso) ?: return "—"
    return java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
        .format(java.util.Date(millis))
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
    val role: String = "renter",
    // "female" | "male". El backend aún no lo persiste (User sin género);
    // se manda igual para que quede registrado cuando agreguen el campo.
    val gender: String? = null
)
data class ForgotPasswordRequest(val email: String)
// La respuesta trae resetToken SOLO en desarrollo (en prod llega por correo).
data class ForgotPasswordResponse(val message: String? = null, val resetToken: String? = null)
data class ResetPasswordRequest(val token: String, val newPassword: String)
data class UserDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = "renter",
    val phone: String? = null,
    val avatar: String? = null,
    // Estado KYC: "not_submitted" | "pending" | "approved" | "rejected".
    val kycStatus: String? = "not_submitted"
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
        imageUrl = images.firstOrNull(),
        district = location.district,
        lat = location.lat,
        lng = location.lng,
        depositAmount = depositAmount?.toInt() ?: 200,
        reviewsCount = reviewsCount
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
    val vehicleImage: String? = null,
    val vehicleRated: Boolean = false,
    val vehicleRating: Int? = null
) {
    fun toDomain() = Reservation(
        id = id.toString(),
        vehicleName = vehicleName ?: "Vehículo",
        startDate = shortDate(startDate),
        endDate = shortDate(endDate),
        total = totalPrice.toInt(),
        status = rentalStatusDisplay(status),
        startMillis = parseIsoUtc(startDate),
        ownerId = ownerId,
        vehicleRated = vehicleRated,
        vehicleRating = vehicleRating
    )

    /**
     * Rango ocupado que esta reserva impone sobre el vehículo, o null si no bloquea
     * (cancelled/completed liberan fechas) o las fechas no son parseables.
     */
    fun toBusyRangeOrNull(): BusyRange? {
        if (status.lowercase() !in setOf("pending", "accepted", "active")) return null
        val s = parseIsoUtc(startDate) ?: return null
        val e = parseIsoUtc(endDate) ?: return null
        return BusyRange(s, e)
    }
}

/** PATCH /rentals/{id}: solo los campos a cambiar (fechas en ISO UTC). */
data class PatchRentalRequest(
    val status: String? = null,
    val completedAt: String? = null,
    val acceptedAt: String? = null,
    val vehicleRated: Boolean? = null,
    val vehicleRating: Int? = null
)

/**
 * POST /payments. Se usa para registrar el reembolso automático al cancelar:
 * el propietario (payer) devuelve al arrendatario (recipient) según la política.
 */
data class CreatePaymentRequest(
    val payerId: Int,
    val recipientId: Int,
    val rentalId: Int,
    val amount: Double,
    val paymentMethod: String = "yape",
    val type: String = "refund",
    val status: String = "completed",
    val description: String? = null,
    val currency: String = "PEN"
)

data class CreatedPaymentDto(
    val id: Int = 0,
    val status: String = ""
)

/** Pago existente de una reserva (GET /payments/rental/{id}). */
data class PaymentRecordDto(
    val id: Int = 0,
    val amount: Double = 0.0,
    val status: String = "",
    val type: String = ""
) {
    /** Cuenta como dinero cobrado al arrendatario (reembolsable). */
    val isCharge: Boolean
        get() = type != "refund" && status.lowercase() in setOf("completed", "success", "paid")
}

// GET /users/{id}: perfil extendido. reputation/onTimeRate/badges llegan cuando el backend
// nuevo esté desplegado; con el deploy anterior vienen ausentes (null) y la app usa su fallback.
data class UserDetailDto(
    val id: Int = 0,
    val kycStatus: String? = null,
    val kycRejectionReason: String? = null,
    val stats: UserStatsDto? = null
)

data class UserStatsDto(
    val completedRentals: Int = 0,
    val reputation: Double? = null,
    val onTimeRate: Double? = null,
    val badges: List<String>? = null
)

/** Badges del backend → etiquetas en español para los chips del perfil. */
internal fun badgeLabel(code: String): String = when (code.uppercase()) {
    "VERIFIED" -> "Verificado"
    "PUNCTUAL" -> "Puntual"
    "TOP_RENTER" -> "Top arrendatario"
    "FIVE_STARS" -> "5 estrellas"
    else -> code.lowercase().replaceFirstChar { it.uppercase() }
}

// GET /rentals/{id}/invoice: comprobante oficial server-side (US25), numeración WPE-{año}-{idPago}.
data class InvoiceDto(
    val invoiceNumber: String = "",
    val issuedAt: String? = null,
    val rentalId: Int = 0,
    val status: String = "",
    val customer: InvoiceCustomerDto = InvoiceCustomerDto(),
    val vehicle: InvoiceVehicleDto = InvoiceVehicleDto(),
    val period: InvoicePeriodDto = InvoicePeriodDto(),
    val payment: InvoicePaymentDto = InvoicePaymentDto(),
    val amount: InvoiceAmountDto = InvoiceAmountDto()
)

data class InvoiceCustomerDto(val fullName: String = "", val dni: String? = null, val email: String? = null)
data class InvoiceVehicleDto(val name: String = "", val licensePlate: String? = null)
data class InvoicePeriodDto(val start: String? = null, val end: String? = null, val days: Int = 0)
data class InvoicePaymentDto(val id: Int = 0, val method: String? = null, val currency: String? = null, val transactionId: String? = null)
data class InvoiceAmountDto(val total: Double = 0.0, val currency: String = "PEN")

// POST /payments/{id}/refund: el backend aplica la política (≥48h→100%, 24-48h→50%, <24h→422),
// marca el pago como refunded, crea el movimiento de reembolso y notifica a ambas partes.
data class RefundRequest(val reason: String? = null)
data class RefundResponse(
    val refundedAmount: Double = 0.0,
    val policy: String = "",
    val status: String = ""
)

/** Instante actual en ISO 8601 UTC (formato que espera el backend). */
internal fun nowIsoUtc(): String =
    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }.format(java.util.Date())

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
        ownerId = ownerId,
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

// El backend exige passengerId: crea una solicitud PENDING (el asiento se descuenta al aceptar).
data class BookSeatBody(val passengerId: Int, val seats: Int)

// Respuesta del book: la solicitud creada (no la ruta).
data class BookRequestDto(
    val id: Int = 0,
    val passengerId: Int = 0,
    val status: String = "PENDING",
    val seats: Int = 1
)

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

// ===== OPERATIONS (backend /Notifications, /messages, /Reviews, /user-reviews) =====
data class ReviewDto(
    val author: String,
    val rating: Int,
    val comment: String,
    val date: String
) {
    fun toDomain() = Review(author, rating, comment, date)
}

// Reseña entre usuarios (UserReviewResource del backend).
data class UserReviewResourceDto(
    val id: Int = 0,
    val reviewerId: Int = 0,
    val reviewedUserId: Int = 0,
    val rentalId: Int = 0,
    val rating: Int = 0,
    val comment: String = "",
    val type: String = "",
    val createdAt: String? = null,
    val reviewerName: String? = null
) {
    fun toDomain() = Review(
        author = reviewerName ?: "Usuario MOVEO",
        rating = rating,
        comment = comment,
        date = relativeTime(createdAt)
    )
}

// Reseña de alquiler/vehículo (ReviewResource del backend).
data class VehicleReviewResourceDto(
    val id: Int = 0,
    val rentalId: Int = 0,
    val vehicleId: Int? = null,
    val reviewerId: Int = 0,
    val revieweeId: Int = 0,
    val rating: Int = 0,
    val comment: String = "",
    val type: String = "",
    val createdAt: String? = null,
    val reviewerName: String? = null
) {
    fun toDomain() = Review(
        author = reviewerName ?: "Usuario MOVEO",
        rating = rating,
        comment = comment,
        date = relativeTime(createdAt)
    )
}

// Request que la UI manda al repositorio; este resuelve a quién y por qué vía calificar.
data class SubmitReviewRequest(
    val targetUserId: String,
    val reservationId: String?,
    val routeId: String?,
    val rating: Int,
    val comment: String
)

// POST /Reviews (reseña de un alquiler; alimenta el rating del vehículo).
data class CreateVehicleReviewRequest(
    val rentalId: Int,
    val vehicleId: Int?,
    val reviewerId: Int,
    val revieweeId: Int,
    val rating: Int,
    val comment: String,
    val type: String = "renter_to_owner"
)

// POST /user-reviews (reseña entre usuarios; se usa para carpool, sin rental asociado).
data class CreateUserReviewRequest(
    val reviewerId: Int,
    val reviewedUserId: Int,
    val rentalId: Int = 0,
    val rating: Int,
    val comment: String,
    val type: String = "renter_to_owner"
)

// Modelo que pinta la UI de notificaciones (el mock lo construye directo).
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val read: Boolean
)

// NotificationResource del backend -> modelo de UI (time relativo desde createdAt).
data class NotificationResourceDto(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val body: String = "",
    val type: String = "",
    val read: Boolean = false,
    val createdAt: String? = null
) {
    fun toUi() = NotificationDto(
        id = id.toString(),
        title = title,
        body = body,
        time = relativeTime(createdAt),
        read = read
    )
}

// Modelo que pinta la UI del chat (el mock lo construye directo).
data class ChatMessageDto(
    val id: String,
    val from: String,
    val body: String,
    val time: String,
    val mine: Boolean
)

// MessageResource del backend -> burbuja de chat ("mine" = lo envió el usuario logueado).
data class MessageResourceDto(
    val id: Int = 0,
    val senderId: Int = 0,
    val receiverId: Int = 0,
    val content: String = "",
    val read: Boolean = false,
    val createdAt: String? = null
) {
    fun toUi(myId: Int) = ChatMessageDto(
        id = id.toString(),
        from = senderId.toString(),
        body = content,
        time = localHourMinute(createdAt),
        mine = senderId == myId
    )
}

// POST /messages (CreateMessageResource del backend).
data class SendMessageRequest(
    val senderId: Int,
    val receiverId: Int,
    val content: String
)

data class TrackingPointDto(val lat: Double, val lng: Double, val time: String)

// ===== SOPORTE / EMERGENCIA (backend /support-tickets) =====
// POST /support-tickets. Se usa para la alerta de emergencia (US08):
// type/category "emergency" y prioridad "urgent" para que soporte lo priorice.
data class CreateSupportTicketRequest(
    val userId: Int,
    val subject: String,
    val description: String,
    val category: String? = null,
    val priority: String? = null,
    val type: String? = null,
    val relatedId: Int? = null,
    val relatedType: String? = null
)
