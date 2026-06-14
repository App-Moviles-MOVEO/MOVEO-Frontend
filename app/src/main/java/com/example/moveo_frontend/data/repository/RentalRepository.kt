package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.BusyRange
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.data.remote.api.RentalApi
import com.example.moveo_frontend.data.remote.dto.CreateRentalRequest
import com.example.moveo_frontend.data.remote.dto.PaymentResponse
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.data.remote.dto.RentalPayRequest
import com.example.moveo_frontend.data.remote.dto.bodyTypeQuery
import com.example.moveo_frontend.data.remote.dto.parseIsoUtc
import com.example.moveo_frontend.data.session.SessionManager
import kotlinx.coroutines.delay

class RentalRepository(
    private val api: RentalApi,
    private val session: SessionManager
) {
    private fun mock() = BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_RENTAL

    private suspend fun currentUserId(): Int =
        session.userIdBlocking()?.toIntOrNull() ?: error("No hay sesión activa")

    suspend fun vehicles(type: String? = null, district: String? = null): Result<List<Vehicle>> = runCatching {
        if (mock()) {
            delay(400)
            return@runCatching MockData.vehicles
                .filter { type == null || it.type == type }
                .filter { district == null || it.location.contains(district, ignoreCase = true) }
        }
        // El backend filtra por bodyType ("compact"/"sedan"/...) y district (Contains).
        api.list(bodyType = bodyTypeQuery(type), district = district).map { it.toDomain() }
    }

    /** Fechas ocupadas de un vehículo (reservas pending/accepted/active). */
    suspend fun busyRanges(vehicleId: String): Result<List<BusyRange>> = runCatching {
        if (mock()) return@runCatching emptyList()
        api.rentals(vehicleId = vehicleId.toIntOrNull() ?: return@runCatching emptyList())
            .mapNotNull { it.toBusyRangeOrNull() }
    }

    /**
     * Fechas ocupadas de TODOS los vehículos (vehicleId -> rangos), para filtrar el
     * catálogo por disponibilidad. Una sola llamada a GET /rentals.
     */
    suspend fun allBusyRanges(): Result<Map<Int, List<BusyRange>>> = runCatching {
        if (mock()) return@runCatching emptyMap()
        api.rentals().groupBy({ it.vehicleId }, { it.toBusyRangeOrNull() })
            .mapValues { (_, ranges) -> ranges.filterNotNull() }
    }

    suspend fun vehicle(id: String): Result<Vehicle> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.vehicles.firstOrNull { it.id == id }
                ?: error("Vehículo no encontrado")
        }
        api.detail(id).toDomain()
    }

    suspend fun publish(req: PublishVehicleRequest): Result<Vehicle> = runCatching {
        // Publicar vehículo es flujo de propietario; se hará en otra app/pantalla.
        // Se mantiene como demo local para no bloquear el flujo del arrendatario.
        delay(700)
        Vehicle(
            id = "v_new_${System.currentTimeMillis()}",
            brand = req.brand, model = req.model, year = req.year,
            pricePerDay = req.pricePerDay, rating = 5.0, ownerName = "Tú",
            ownerVerified = true, location = req.location, type = req.type,
            transmission = req.transmission, seats = req.seats, fuel = req.fuel,
            description = req.description, imageEmoji = "🚗"
        )
    }

    /**
     * Crea la reserva en el backend (POST /rentals). Necesita ownerId (del vehículo) y el total.
     * renterId sale de la sesión. Fechas en ISO 8601 UTC.
     * Antes de crear, verifica disponibilidad contra las reservas existentes del vehículo
     * (el backend aún no rechaza solapamientos con 409 — ver BACKEND_REQUESTS.md P1).
     */
    suspend fun reserve(
        vehicleId: String,
        ownerId: Int,
        startDate: String,
        endDate: String,
        totalPrice: Int,
        pickupLocation: String?
    ): Result<Reservation> = runCatching {
        if (mock()) {
            delay(600)
            val v = MockData.vehicles.firstOrNull { it.id == vehicleId }
            return@runCatching Reservation(
                id = "res_${System.currentTimeMillis()}",
                vehicleName = v?.let { "${it.brand} ${it.model}" } ?: "Vehículo",
                startDate = startDate, endDate = endDate,
                total = totalPrice, status = "Confirmado"
            )
        }
        val start = parseIsoUtc(startDate)
        val end = parseIsoUtc(endDate)
        if (start != null && end != null) {
            val busy = busyRanges(vehicleId).getOrDefault(emptyList())
            if (busy.any { it.overlaps(start, end) }) {
                error("Este auto ya está reservado en esas fechas. Elige otro rango.")
            }
        }
        api.createRental(
            CreateRentalRequest(
                vehicleId = vehicleId.toInt(),
                renterId = currentUserId(),
                ownerId = ownerId,
                startDate = startDate,
                endDate = endDate,
                totalPrice = totalPrice.toDouble(),
                pickupLocation = pickupLocation,
                returnLocation = pickupLocation
            )
        ).toDomain()
    }

    /** Pago en un paso de una reserva (Yape/tarjeta/efectivo). POST /rentals/{id}/pay. */
    suspend fun pay(reservationId: String, method: String, amount: Int): Result<PaymentResponse> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching PaymentResponse(id = "pay_${System.currentTimeMillis()}", status = "success")
        }
        val res = api.payRental(reservationId, RentalPayRequest(paymentMethod = method, amount = amount.toDouble()))
        PaymentResponse(
            id = res.payment?.id?.toString() ?: reservationId,
            status = res.payment?.status ?: "completed"
        )
    }

    /** Vehículos publicados por el usuario actual. (Backend pendiente; por ahora mock.) */
    suspend fun myVehicles(): Result<List<Vehicle>> = runCatching {
        delay(300)
        MockData.myVehicles
    }

    suspend fun myReservations(): Result<List<Reservation>> = runCatching {
        if (mock()) {
            delay(400)
            return@runCatching MockData.reservations
        }
        api.userRentals(currentUserId()).map { it.toDomain() }
    }

    suspend fun reservation(id: String): Result<Reservation> = runCatching {
        if (mock()) {
            delay(300)
            return@runCatching MockData.reservations.firstOrNull { it.id == id }
                ?: error("Reserva no encontrada")
        }
        api.rental(id).toDomain()
    }
}