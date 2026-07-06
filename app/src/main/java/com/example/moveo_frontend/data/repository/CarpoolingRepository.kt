package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.CarpoolBooking
import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.remote.api.CarpoolingApi
import com.example.moveo_frontend.data.remote.dto.BookSeatBody
import com.example.moveo_frontend.data.remote.dto.CreateCarpoolRequest
import com.example.moveo_frontend.data.remote.dto.CreatePaymentRequest
import com.example.moveo_frontend.data.remote.dto.PublishRouteRequest
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto
import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CarpoolingRepository(
    private val api: CarpoolingApi,
    private val session: SessionManager
) {
    private fun mock() = BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING

    // El backend no rastrea las reservas de carpool por usuario, así que se guardan en memoria
    // para mostrarlas en "Mis reservas" durante la sesión.
    private val bookings = mutableListOf<CarpoolBooking>()

    private suspend fun currentUserId(): Int =
        session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")

    suspend fun routes(
        onlyWomen: Boolean? = null,
        verified: Boolean? = null,
        community: String? = null
    ): Result<List<CarpoolRoute>> = runCatching {
        if (mock()) {
            delay(400)
            return@runCatching MockData.routes.filter {
                (onlyWomen != true || it.onlyWomen) &&
                    (verified != true || it.verified) &&
                    (community.isNullOrBlank() || it.community.equals(community, ignoreCase = true))
            }
        }
        // El backend filtra por onlyWomen y community; "verified" se ignora (todas se muestran).
        api.list(
            type = "carpool",
            onlyWomen = onlyWomen.takeIf { it == true },
            community = community?.takeIf { it.isNotBlank() }
        ).map { it.toDomain() }
    }

    suspend fun route(id: String): Result<CarpoolRoute> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.routes.firstOrNull { it.id == id }
                ?: error("Ruta no encontrada")
        }
        api.detail(id).toDomain()
    }

    suspend fun publish(req: PublishRouteRequest): Result<CarpoolRoute> = runCatching {
        if (mock()) {
            delay(600)
            return@runCatching CarpoolRoute(
                id = "r_new_${System.currentTimeMillis()}",
                driverName = "Tú", driverRating = 5.0, verified = true,
                origin = req.origin, destination = req.destination,
                departureTime = req.time, date = req.date,
                seatsAvailable = req.seats, pricePerSeat = req.price,
                vehicleModel = "Mi vehículo", community = "UPC"
            )
        }
        api.publish(
            CreateCarpoolRequest(
                ownerId = currentUserId(),
                name = "carpool-${System.currentTimeMillis()}",
                title = "${req.origin} → ${req.destination}",
                description = if (req.recurring) "Viaje recurrente" else "Viaje único",
                startLocation = req.origin,
                endLocation = req.destination,
                departureDate = toIsoDate(req.date),
                departureTime = req.time,
                seatsTotal = req.seats,
                seatsAvailable = req.seats,
                pricePerSeat = req.price.toDouble()
            )
        ).toDomain()
    }

    /** Viajes publicados por el usuario actual. (Backend pendiente; por ahora mock.) */
    suspend fun myRoutes(): Result<List<CarpoolRoute>> = runCatching {
        delay(300)
        MockData.myRoutes
    }

    suspend fun book(route: CarpoolRoute, seats: Int): Result<Unit> = runCatching {
        if (!mock()) {
            val me = currentUserId()
            // 1) Crea la solicitud PENDING; el conductor la acepta/rechaza y recién ahí
            // se descuenta el asiento. 400 sin passengerId, 409 si no hay cupo/duplicada.
            // Se valida el aforo aquí (antes de cobrar) para no cobrar sin cupo.
            api.book(route.id, BookSeatBody(passengerId = me, seats = seats))
            // 2) US23: prepago de la cuota al enviar la solicitud. El pasajero paga al
            // conductor (recipient) por adelantado; queda registrado en /payments.
            api.pay(
                CreatePaymentRequest(
                    payerId = me,
                    recipientId = route.ownerId,
                    rentalId = 0,
                    amount = (route.pricePerSeat * seats).toDouble(),
                    paymentMethod = "yape",
                    type = "carpool_seat",
                    status = "completed",
                    description = "Cuota de asiento · ${route.origin} → ${route.destination} ($seats)"
                )
            )
        } else {
            delay(500)
        }
        // Registrar la reserva localmente para que aparezca en "Mis reservas".
        bookings.add(
            0,
            CarpoolBooking(
                routeId = route.id,
                origin = route.origin,
                destination = route.destination,
                date = route.date,
                departureTime = route.departureTime,
                driverName = route.driverName,
                seats = seats,
                total = route.pricePerSeat * seats
            )
        )
    }

    /** Reservas de carpool del usuario hechas en esta sesión. */
    suspend fun myBookings(): Result<List<CarpoolBooking>> = runCatching { bookings.toList() }

    suspend fun tracking(routeId: String): Result<List<TrackingPointDto>> = runCatching {
        // El backend no expone tracking en vivo; se mantiene demo local.
        delay(400)
        MockData.trackingPoints
    }

    /** Normaliza una fecha de texto a ISO 8601 UTC; si no es parseable usa la fecha de hoy. */
    private fun toIsoDate(text: String): String {
        val out = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val candidate = text.take(10)
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            out.format(parser.parse(candidate) ?: Date())
        } catch (_: Exception) {
            out.format(Date())
        }
    }
}